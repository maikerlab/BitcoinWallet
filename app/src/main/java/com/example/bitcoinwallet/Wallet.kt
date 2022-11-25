package com.example.bitcoinwallet

import android.util.Log
import org.bitcoindevkit.*
import org.bitcoindevkit.Wallet as BdkWallet

data class WalletConfig(val dbPath: String)

class Wallet private constructor(private val config: WalletConfig?) {

    private lateinit var bdkWallet: BdkWallet
    private var electrumURL: String = "ssl://electrum.blockstream.info:60002"
    var fingerprint: String = ""

    companion object {
        const val TAG = "Wallet"
        private var instance: Wallet? = null

        fun getInstance(config: WalletConfig?): Wallet {
            if (instance == null)
                instance = Wallet(config)
            return instance!!
        }
    }

    object LogProgress: Progress {
        override fun update(progress: Float, message: String?) {
            Log.i(TAG, "Sync wallet")
        }
    }

    fun createNewWallet() {
        // Create mnemonic
        val mnemonic = Mnemonic(WordCount.WORDS12)
        loadWallet(mnemonic)
    }

    fun restoreFromSeedWords(seedWords: String) {
        val mnemonic = Mnemonic.fromString(seedWords)
        loadWallet(mnemonic)
    }

    private fun loadWallet(mnemonic: Mnemonic) {
        // Create root key
        val bip32RootKey: DescriptorSecretKey = DescriptorSecretKey(
            network = Network.TESTNET,
            mnemonic = mnemonic,
            password = ""
        )

        // Create Descriptor (external)
        val descriptor = createDescriptor(bip32RootKey, "m/84h/1h/0h/0")
        Log.d(TAG, descriptor)

        // Create Descriptor (change)
        val changeDescriptor = createDescriptor(bip32RootKey, "m/84h/1h/0h/1")
        Log.d(TAG, changeDescriptor)

        // Init Wallet
        initialize(descriptor, changeDescriptor)
    }

    private fun initialize(descriptor: String, changeDescriptor: String) {
        // TODO: App crashes when using SQLite
        val sqliteDatabaseConfig = DatabaseConfig.Sqlite(SqliteDbConfiguration("${config?.dbPath}/bdk.sqlite"))
        val inMemoryConfig = DatabaseConfig.Memory

        bdkWallet = BdkWallet(
            descriptor = descriptor,
            changeDescriptor = changeDescriptor,
            network = Network.TESTNET,
            databaseConfig = inMemoryConfig,
        )
    }

    fun sync() {
        val blockchainConfig: BlockchainConfig = BlockchainConfig.Electrum(
            ElectrumConfig(
                electrumURL,
                null,
                5u,
                null,
                10u
            )
        )
        val blockchain: Blockchain = Blockchain(blockchainConfig)
        blockchain.getBlockHash((12).toUInt())
        bdkWallet.sync(blockchain, LogProgress)
    }

    fun getNewAddress(): String {
        return bdkWallet.getAddress(AddressIndex.NEW).address
    }

    fun getNetwork(): String {
        return bdkWallet.network().toString()
    }

    fun getBalance(): ULong {
        return bdkWallet.getBalance().total
    }

    private fun createDescriptor(rootKey: DescriptorSecretKey, path: String): String {
        val bip84ExternalPath: DerivationPath = DerivationPath(path)
        val externalExtendedKey: DescriptorSecretKey = rootKey.extend(bip84ExternalPath)
        fingerprint = externalExtendedKey.asPublic().asString().substring(1, 9)
        return "wpkh(${externalExtendedKey.asString()})"
    }

}


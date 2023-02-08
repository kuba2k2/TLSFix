package pl.szczodrzynski.tlsfix

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers.findAndHookMethod
import de.robv.android.xposed.callbacks.XC_LoadPackage

class NativeCryptoHook : IXposedHookLoadPackage {

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        if (lpparam == null)
            return

        findAndHookMethod(
            "com.android.org.conscrypt.NativeCrypto",
            lpparam.classLoader,
            "getDefaultProtocols",
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    val protocols = param.result as Array<String>
                    XposedBridge.log("TLSFix: default protocols: ${protocols.joinToString()}")
                    param.result = arrayOf("SSLv3", "TLSv1", "TLSv1.1", "TLSv1.2")
                }
            },
        )
        XposedBridge.log("TLSFix: hooked package ${lpparam.packageName}")
    }
}

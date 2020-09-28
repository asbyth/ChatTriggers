package com.chattriggers.ctjs.launch

import com.chattriggers.ctjs.engine.module.ModuleManager
import net.minecraft.crash.CrashReportCategory

object AsmUtils {
    fun addCrashSectionCallable(crashReport: CrashReportCategory) {
        crashReport.addCrashSection("ct.js modules", ModuleManager.cachedModules.toString())
    }
}

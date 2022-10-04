package com.fortechteams.kcloak.util

@RequiresOptIn(
  message = "This DSL is experimental. It may be changed, discontinued or removed in the future without notice.",
  RequiresOptIn.Level.WARNING
)
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class ExperimentalDsl

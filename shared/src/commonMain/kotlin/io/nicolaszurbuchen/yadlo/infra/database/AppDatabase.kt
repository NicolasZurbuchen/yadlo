package io.nicolaszurbuchen.yadlo.infra.database

import io.nicolaszurbuchen.yadlo.cache.AppDatabase

fun createDatabase(driverFactory: DatabaseDriverFactory): AppDatabase {
    val driver = driverFactory.createDriver()
    return AppDatabase(
        driver = driver,
    )
}

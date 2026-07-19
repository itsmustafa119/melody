package com.mustafa.melody.core.navigation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AppDestinationTest {

    @Test
    fun `should have exactly five top-level destinations`() {
        assertEquals(5, AppDestination.entries.size)
    }

    @Test
    fun `all destination routes should be unique`() {
        val routes = AppDestination.entries.map { it.route }
        assertEquals(routes.size, routes.distinct().size)
    }

    @Test
    fun `HOME should be the first destination`() {
        assertEquals(AppDestination.HOME, AppDestination.entries.first())
    }

    @Test
    fun `every destination should have a valid label resource ID`() {
        AppDestination.entries.forEach { destination ->
            assertTrue(destination.labelResId != 0)
        }
    }
}

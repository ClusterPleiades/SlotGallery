package com.pleiades.pleione.slotgallery.extension

import org.junit.Assert

fun testAssertEquals(expected: Any, actual: Any) {
    Assert.assertEquals(expected, actual)
}

fun testAssertTrue(condition: Boolean) {
    Assert.assertTrue(condition)
}

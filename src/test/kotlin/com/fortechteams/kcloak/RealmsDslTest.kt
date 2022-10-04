package com.fortechteams.kcloak

import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.*

internal class RealmsDslTest {

  private val realmsDsl = KCloak
    .of(KCUtil.localKeycloakBuilder)
    .realms

  @Test
  fun all() {
    assertTrue(realmsDsl.all().isNotEmpty())
  }

  @Test
  fun `get all and all representations`() {
    val name = "RealmsDslTest_${UUID.randomUUID()}"
    realmsDsl.create(name) {}

    assertEquals(1, realmsDsl.all().count { it.representation().realm == name })
    assertEquals(1, realmsDsl.allRepresentations().count { it.realm == name })
  }

  @Test
  fun exists() {
    val name = "RealmsDslTest_${UUID.randomUUID()}"
    realmsDsl.create(name) {}

    assertTrue(realmsDsl.exists(name))
    assertFalse(realmsDsl.exists("does-not-exist_${UUID.randomUUID()}"))
  }

  @Test
  fun get() {
    val name = "RealmsDslTest_${UUID.randomUUID()}"
    realmsDsl.create(name) {}

    assertNotNull(realmsDsl.get(name))
    assertNull(realmsDsl.get("does-not-exist_${UUID.randomUUID()}"))
  }

  @Test
  fun `get representation`() {
    val name = "RealmsDslTest_${UUID.randomUUID()}"
    realmsDsl.create(name) {}

    val rep = realmsDsl.getRepresentation(name)
    assertNotNull(rep)
    assertEquals(name, rep.realm)
    assertNull(realmsDsl.getRepresentation("does-not-exist_${UUID.randomUUID()}"))
  }

  @Test
  fun `get or create`() {
    val name1 = "RealmsDslTest_${UUID.randomUUID()}"
    val name2 = "RealmsDslTest_${UUID.randomUUID()}"

    realmsDsl.create(name1) {
      isEnabled = true
    }

    val rep1 = realmsDsl.getOrCreate(name1)

    assertNotNull(rep1)
    assertEquals(name1, rep1.representation().realm)
    assertTrue(rep1.representation().isEnabled)

    val rep2 = realmsDsl.getOrCreate(name2)

    assertNotNull(rep1)
    assertEquals(name2, rep2.representation().realm)
    assertFalse(rep2.representation().isEnabled)
  }

  @Test
  fun ensure() {
    val name1 = "RealmsDslTest_${UUID.randomUUID()}"

    val r1 = realmsDsl.ensure(name1) {
      isEnabled = true
    }

    val r2 = realmsDsl.ensure(name1) {
      isEnabled = true
    }

    assertTrue(r1.representation().isEnabled)
    assertEquals(r1.representation().id, r2.representation().id)
  }
}

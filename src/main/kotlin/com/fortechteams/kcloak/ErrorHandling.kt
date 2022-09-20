package com.fortechteams.kcloak

import com.fortechteams.kcloak.exception.NotFoundException
import com.fortechteams.kcloak.exception.PermissionException
import javax.ws.rs.ForbiddenException
import javax.ws.rs.NotAllowedException
import javax.ws.rs.NotFoundException as JaxNotFoundException

class ErrorHandler(
  private val resourceName: String
) {

  fun <T> wrap(fn: () -> T) = try {
    fn()
  } catch (e: ForbiddenException) {
    throw PermissionException(e)
  } catch (e: NotAllowedException) {
    throw PermissionException(e)
  } catch (e: JaxNotFoundException) {
    throw NotFoundException("$resourceName was not found!", e)
  }
}

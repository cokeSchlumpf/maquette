package maquette.controller.values

import java.time.Instant

sealed trait Authorization {

}

case class UserAuthorization(id: String)

case class GrantedAuthorization(authorization: Authorization, by: User, at: Instant)

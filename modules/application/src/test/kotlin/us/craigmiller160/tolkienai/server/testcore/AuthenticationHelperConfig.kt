package us.craigmiller160.tolkienai.server.testcore

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import us.craigmiller160.testcontainers.common.core.AuthenticationHelper

@Configuration
class AuthenticationHelperConfig {
  @Bean fun authenticationHelper(): AuthenticationHelper = AuthenticationHelper()

  @Bean
  fun defaultUsers(authHelper: AuthenticationHelper): DefaultUsers {
    val primaryUser = authHelper.createUser("primary@gmail.com").let { authHelper.login(it) }
    return DefaultUsers(primaryUser = primaryUser)
  }
}

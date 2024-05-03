package us.craigmiller160.tolkienai.server.testcore

import io.mockk.clearAllMocks
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext

class MockExtension : BeforeEachCallback {

  override fun beforeEach(ctx: ExtensionContext) {
    clearAllMocks()
  }
}

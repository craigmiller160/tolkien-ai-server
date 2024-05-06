package us.craigmiller160.tolkienai.server.migration

interface Migration<T> {
  fun migrate(helper: T)
}

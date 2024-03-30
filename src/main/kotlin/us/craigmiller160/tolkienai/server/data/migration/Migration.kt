package us.craigmiller160.tolkienai.server.data.migration

interface Migration<T> {
  fun migrate(helper: T)
}

val pluginSbtScoverageVersion = sys.props.getOrElse(
  "plugin.sbtscoverage.version",
  "2.0.6"
)
addSbtPlugin("org.scoverage" % "sbt-scoverage" % pluginSbtScoverageVersion)
addSbtPlugin("nl.gn0s1s" % "sbt-dotenv" % "3.0.0")

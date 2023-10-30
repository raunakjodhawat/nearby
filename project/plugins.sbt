val pluginSbtScoverageVersion = sys.props.getOrElse(
  "plugin.sbtscoverage.version",
  "2.0.6"
)
addSbtPlugin("org.scoverage" % "sbt-scoverage" % pluginSbtScoverageVersion)

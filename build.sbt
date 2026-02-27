ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.12.18"

libraryDependencies += "org.apache.spark" %% "spark-core" % "3.5.7"
libraryDependencies += "org.apache.spark" %% "spark-sql" % "3.5.7" exclude("org.apache.commons", "commons-text")
libraryDependencies += "org.apache.spark" %% "spark-mllib" % "3.5.7"
libraryDependencies += "org.apache.kafka" % "kafka-clients" % "4.1.1"
libraryDependencies += "org.apache.spark" %% "spark-sql-kafka-0-10" % "3.5.7"
libraryDependencies += "org.apache.spark" %% "spark-sketch" % "3.5.7"

// https://mvnrepository.com/artifact/org.apache.sedona/sedona-sql-3.0
libraryDependencies += "org.apache.sedona" %% "sedona-sql-3.0" % "1.4.1"

// GeoTools libs (replace fat wrapper) to provide org.opengis.* classes
libraryDependencies += "org.geotools" % "gt-main" % "28.2" exclude("javax.media", "jai_core")
libraryDependencies += "org.geotools" % "gt-referencing" % "28.2" exclude("javax.media", "jai_core")

resolvers += "OSGeo" at "https://repo.osgeo.org/repository/release/"

// Avoid pulling an older commons-text via transitive deps
libraryDependencies += "org.apache.commons" % "commons-text" % "1.11.0"
ThisBuild / dependencyOverrides += "org.apache.commons" % "commons-text" % "1.11.0"

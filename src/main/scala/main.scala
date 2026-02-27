import org.apache.spark.sql.SparkSession
import org.apache.sedona.sql.utils.SedonaSQLRegistrator
// import java.nio.file.{Files, Paths}
// import java.nio.charset.StandardCharsets

object main {
  def main(args: Array[String]): Unit = {

    println("Spark + Sedona works.")

    System.setProperty("hadoop.home.dir", "C:\\hadoop")


    val spark = SparkSession.builder()
      .appName("SedonaTest")
      .master("local[*]")
      .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .config("spark.kryo.registrator", "org.apache.sedona.core.serde.SedonaKryoRegistrator")
      .getOrCreate()

      spark.sparkContext.setLogLevel("ERROR")

    //  Sedona SQL functions
    SedonaSQLRegistrator.registerAll(spark)

    // read the finalData.csv file into a dataframe
    val df = spark.read
      .option("header", "true")
      .option("inferSchema", "true")
      .csv("src/data/finalData.csv")

    
    df.createOrReplaceTempView("data_table")

    val selectedDf = df.select("sector", "category", "subcategory","areaNumber","areaName","areaPopulation","Latitude","Longitude") 

    // drop values with null latitude or longitude
    val filteredDf = selectedDf.na.drop(Seq("Latitude", "Longitude"))
    println("--------FILTERED DATAFRAME--------")
    filteredDf.show(20)

    filteredDf.createOrReplaceTempView("filtered_data_table")

//     // Sedona to create point geometries from latitude and longitude
//     spark.sql(
//       """
//         |SELECT sector, category, subcategory, areaNumber, areaName, areaPopulation,
//         |ST_Point(CAST(Longitude AS Decimal(24,20)), CAST(Latitude AS Decimal(24,20))) AS geom
//         |FROM filtered_data_table
//       """.stripMargin).createOrReplaceTempView("spatial_data_table")


//     println("--------SPATIAL DATAFRAME--------")
//     val spatialDf = spark.sql("SELECT * FROM spatial_data_table")
//     spatialDf.show(20, false)


//     // count nearby competitors within 0.01 degree (~1.11 km) for each business where the profession matches
//     println("--------NEARBY COMPETITORS COUNT--------")
//     val nearbyDf = spark.sql(
//       """
//       SELECT a.sector,
//        a.category,
//        a.subcategory,
//        a.areaName,
//        a.areaPopulation,
//        COUNT(b.geom) AS nearbyCompetitorsCount
//       FROM spatial_data_table a
//       LEFT JOIN spatial_data_table b
//         ON ST_Distance(a.geom, b.geom) <= 0.01
//       AND a.areaNumber != b.areaNumber
//       AND a.subcategory = b.subcategory
//       GROUP BY a.sector, a.category, a.subcategory, a.areaName, a.areaPopulation
//       """.stripMargin)
//     nearbyDf.show(20, false)


//     // the avg distance of businesses in the same subcategory
//     println("--------AVERAGE DISTANCE TO SIMILAR BUSINESSES--------")
//     val avgDistanceDf = spark.sql("""
//   SELECT 
//           a.subcategory,
//           AVG(ST_Distance(a.geom, b.geom)) * 111 AS avgDistance
//       FROM spatial_data_table a
//       JOIN spatial_data_table b
//         ON a.subcategory = b.subcategory
//       AND a.areaNumber != b.areaNumber
//       GROUP BY a.subcategory
//       ORDER BY avgDistance ASC
//     """) 
//     avgDistanceDf.show(50, false)

//   // creating a map of the businesses 
//   val geoDf = spark.sql("""
//     SELECT
//       sector,
//       category,
//       subcategory,
//       areaName,
//       areaPopulation,
//       ST_AsGeoJSON(geom) AS geometry
//     FROM spatial_data_table
//   """)
// // a fine way, but it requires hadoop to be set up
//   // geoDf
//   //   .coalesce(1)
//   //   .write
//   //   .mode("overwrite")
//   //   .json("output/points_geojson")

//   val features = geoDf.collect().map { row =>
//       s"""
//       {
//         "type": "Feature",
//         "geometry": ${row.getAs[String]("geometry")},
//         "properties": {
//           "sector": "${row.getAs[String]("sector")}",
//           "category": "${row.getAs[String]("category")}",
//           "subcategory": "${row.getAs[String]("subcategory")}",
//           "areaName": "${row.getAs[String]("areaName")}",
//           "areaPopulation": ${row.getAs[Any]("areaPopulation")}
//         }
//       }
//       """
//     }

//   val geojson =
//       s"""
//       {
//         "type": "FeatureCollection",
//         "features": [
//           ${features.mkString(",")}
//         ]
//       }
//       """

// // write the geojson string to a file
//   Files.write(
//       Paths.get("points.geojson"),
//       geojson.getBytes(StandardCharsets.UTF_8)
//       )
//     println("-------POINTS.GEOJSON FILE CREATED--------")
  
  }
}
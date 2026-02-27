error id: file:///C:/Users/rubae/IdeaProjects/sedona/src/main/scala/main.scala:coalesce.
file:///C:/Users/rubae/IdeaProjects/sedona/src/main/scala/main.scala
empty definition using pc, found symbol in pc: coalesce.
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -geoDf/coalesce.
	 -geoDf/coalesce#
	 -geoDf/coalesce().
	 -scala/Predef.geoDf.coalesce.
	 -scala/Predef.geoDf.coalesce#
	 -scala/Predef.geoDf.coalesce().
offset: 2378
uri: file:///C:/Users/rubae/IdeaProjects/sedona/src/main/scala/main.scala
text:
```scala
import org.apache.spark.sql.SparkSession
import org.apache.sedona.sql.utils.SedonaSQLRegistrator

object main {
  def main(args: Array[String]): Unit = {

    println("Spark + Sedona works.")

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

    // Sedona to create point geometries from latitude and longitude
    spark.sql(
      """
SELECT sector, category, subcategory, areaNumber, areaName, areaPopulation,
ST_Point(CAST(Longitude AS Decimal(24,20)), CAST(Latitude AS Decimal(24,20))) AS geom
FROM filtered_data_table
      """.stripMargin).createOrReplaceTempView("spatial_data_table")


    println("--------SPATIAL DATAFRAME--------")
    val spatialDf = spark.sql("SELECT * FROM spatial_data_table")
    spatialDf.show(20, false)

    println("--------NEARBY COMPETITORS COUNT--------")
      spark.sql("""
  SELECT a.areaName,
        a.subcategory,
        COUNT(*) AS nearbyCompetitors
  FROM spatial_data_table a
  JOIN spatial_data_table b
  ON ST_Distance(a.geom, b.geom) <= 300
  AND a.geom != b.geom
  GROUP BY a.areaName, a.subcategory
  ORDER BY nearbyCompetitors DESC
  """).show(false)


  // creating a map of the businesses 
  val geoDf = spark.sql("""
    SELECT
      sector,
      category,
      subcategory,
      areaName,
      areaPopulation,
      ST_AsGeoJSON(geom) AS geometry
    FROM spatial_data_table
  """)

  geoDf
    .co@@alesce(1)
    .write
    .mode("overwrite")
    .json("output/points_geojson")




  }
}


```


#### Short summary: 

empty definition using pc, found symbol in pc: coalesce.
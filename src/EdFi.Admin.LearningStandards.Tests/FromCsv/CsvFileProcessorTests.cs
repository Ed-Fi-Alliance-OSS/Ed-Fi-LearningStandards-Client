using System.IO;
using System.Linq;
using System.Reflection;
using EdFi.Admin.LearningStandards.Core.Services.FromCsv;
using NUnit.Framework;

namespace EdFi.Admin.LearningStandards.Tests.FromCsv
{
    [TestFixture]
    [SetCulture("en-US")]
    public class CsvFileProcessorTests
    {
        [Test]
        public void Will_throw_file_not_found_error()
        {
            //Arrange
            var csvFileProcessor = new CsvFileProcessor();
          
            //Assert -> Act
            Assert.Throws<FileNotFoundException>( () =>
            {
                var data = csvFileProcessor.GetRows(Path.Combine(GetAssemblyPath(), "TestFiles/Sample-metadata/no-file.csv")).ToList();
            });
        }

        [Test]
        public void Will_read_and_convert_csv_row_as_dictionary_entry()
        {
            //Arrange
            var csvFileProcessor = new CsvFileProcessor();

            //Act
            var data = csvFileProcessor.GetRows(Path.Combine(GetAssemblyPath(), "TestFiles/Sample-metadata/test-data.csv")).ToList();

            //Assert
            Assert.AreEqual(2, data.Count);
            var row1 = data[0];
            var row2 = data[1];
            Assert.AreEqual("LSCSVId-1234", row1["LearningStandardId"]);
            Assert.AreEqual("description2", row2["Description"]);
        }

        [Test]
        public void Will_have_invalid_rows_exception_items()
        {
            //Arrange
            var csvFileProcessor = new CsvFileProcessor();

            //Act
            var data = csvFileProcessor.GetRows(Path.Combine(GetAssemblyPath(), "TestFiles/Sample-metadata/test-invalid-rows.csv")).ToList();

            //Assert
            Assert.AreEqual(1, csvFileProcessor.InvalidRowsExceptions.Count);
            Assert.AreEqual("Field is missing on row number 2", csvFileProcessor.InvalidRowsExceptions.First().Message);
        }

        public static string GetAssemblyPath()
            => Path.GetDirectoryName(Assembly.GetExecutingAssembly().Location);
    }
}

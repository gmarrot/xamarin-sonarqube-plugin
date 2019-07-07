using FluentAssertions;
using NUnit.Framework;
using SonarScannerSample.Models;
using Xamarin.Forms;

namespace SonarScannerSample.Test.Models {
    public class SampleItemTest {

        [Test]
        public void Test_For_ShouldReturnSampleItemWithCorrectProperties_WhenTitleIsNull() {
            // When
            var sampleItem = SampleItem.For<ContentPage>();

            // Then
            sampleItem.Should().NotBeNull();
            sampleItem.Title.Should().Be("ContentPage");
            sampleItem.PageType.Should().Be(typeof(ContentPage));
        }

        [Test]
        public void Test_For_ShouldReturnSampleItemWithCorrectProperties_WhenTitleIsDefined() {
            // Given
            const string PAGE_TITLE = "Content Page";

            // When
            var sampleItem = SampleItem.For<ContentPage>(PAGE_TITLE);

            // Then
            sampleItem.Should().NotBeNull();
            sampleItem.Title.Should().Be(PAGE_TITLE);
            sampleItem.PageType.Should().Be(typeof(ContentPage));
        }

    }
}

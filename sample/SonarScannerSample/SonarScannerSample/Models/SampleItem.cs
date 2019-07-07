using System;
using Xamarin.Forms;

namespace SonarScannerSample.Models {
    public class SampleItem {

        public static SampleItem For<T>(string title = null) where T : Page, new() {
            Type pageType = typeof(T);
            string pageTitle = title ?? pageType.Name;

            return new SampleItem(pageTitle, pageType);
        }

        SampleItem(string title, Type pageType) {
            Title = title ?? throw new ArgumentNullException(nameof(title));
            PageType = pageType ?? throw new ArgumentNullException(nameof(pageType));
        }

        public string Title { get; }

        public Type PageType { get; }

    }
}

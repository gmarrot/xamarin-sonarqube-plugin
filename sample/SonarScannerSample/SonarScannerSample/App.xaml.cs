using System.Collections.Generic;
using SonarScannerSample.Models;
using SonarScannerSample.Pages;
using Xamarin.Forms;

namespace SonarScannerSample {
    public partial class App : Application {

        public App() {
            InitializeComponent();

            var items = new List<SampleItem> {
                SampleItem.For<Page>("Page 1"),
                SampleItem.For<Page>("Page 2"),
                SampleItem.For<Page>("Page 3")
};

            MainPage = new NavigationPage(new MainPage(items));
        }

        protected override void OnStart() {
            // Handle when your app starts
        }

        protected override void OnSleep() {
            // Handle when your app sleeps
        }

        protected override void OnResume() {
            // Handle when your app resumes
        }

    }
}

using System;
using System.Collections.Generic;
using System.ComponentModel;
using SonarScannerSample.Models;
using Xamarin.Forms;

namespace SonarScannerSample.Pages {
    // Learn more about making custom code visible in the Xamarin.Forms previewer
    // by visiting https://aka.ms/xamarinforms-previewer
    [DesignTimeVisible(false)]
    public partial class MainPage : ContentPage {

        public MainPage(IEnumerable<SampleItem> items) {
            InitializeComponent();

            PagesListView.ItemsSource = items;
        }

        void OnItemSelected(object sender, SelectedItemChangedEventArgs args) {
            if (args?.SelectedItem != null) {
                if (args.SelectedItem is SampleItem item) {
                    var selectedPage = Activator.CreateInstance(item.PageType) as Page;
                    if (selectedPage != null) {
                        Navigation.PushAsync(selectedPage, true);
                    }
                }

                if (sender is ListView listView) {
                    listView.SelectedItem = null;
                }
            }
        }

    }
}

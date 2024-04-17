package freiman.earthquake;

import hu.akarnokd.rxjava3.swing.SwingSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import freiman.earthquake.json.Feature;
import freiman.earthquake.json.FeatureCollection;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


public class EarthquakeFrame extends JFrame {

    private JList<String> jlist = new JList<>();
    private ListSelectionModel listSelectionModel;
    private EarthquakeService service = new EarthquakeServiceFactory().getService();
    private FeatureCollection featureCollection;

    public EarthquakeFrame() {

        setTitle("EarthquakeFrame");
        setSize(300, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setLayout(new BorderLayout());


        // Create the radio buttons and add to a group
        JRadioButton lastHour = new JRadioButton("One Hour");
        lastHour.setActionCommand("lastHour");
        JRadioButton lastMonth = new JRadioButton("30 Days");
        lastMonth.setActionCommand("lastMonth");

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(lastHour);
        buttonGroup.add(lastMonth);

        JPanel radioButtonPanel = new JPanel();
        radioButtonPanel.add(lastHour);
        radioButtonPanel.add(lastMonth);

        lastHour.addActionListener(e -> hourActionHandler(e));
        lastMonth.addActionListener(e -> monthActionHandler(e));

        listSelectionModel = jlist.getSelectionModel();
        listSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listSelectionModel.addListSelectionListener(
                new ListSelectionListener() {
                    public void valueChanged(ListSelectionEvent e) {
                        int selIndex = jlist.getSelectedIndex();

                        if (selIndex != -1) {
                            Feature feature = featureCollection.features[selIndex];
                            double longitude = feature.geometry.coordinates[0];
                            double latitude = feature.geometry.coordinates[1];

                            String url = "http://www.google.com/maps/place/" + latitude + "," + longitude;
                            if (Desktop.isDesktopSupported()
                                    && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                                try {
                                    Desktop.getDesktop().browse(new URI(url));
                                } catch (IOException | URISyntaxException ex) {
                                    throw new RuntimeException(ex);
                                }
                            }
                        }
                    }
                });

        // Add to the frame
        add(radioButtonPanel, BorderLayout.NORTH);
        add(jlist, BorderLayout.CENTER);
    }

    private void monthActionHandler(ActionEvent e) {
        Disposable disposable = service.topMonth()
                    .subscribeOn(Schedulers.io())
                    .observeOn(SwingSchedulers.edt())
                    .subscribe(
                            this::handleResponse,
                            Throwable::printStackTrace);
    }

    private void hourActionHandler(ActionEvent e) {
        Disposable disposable = service.oneHour()
                    .subscribeOn(Schedulers.io())
                    .observeOn(SwingSchedulers.edt())
                    .subscribe(
                            this::handleResponse,
                            Throwable::printStackTrace);
    }

    private void handleResponse(FeatureCollection response) {
        featureCollection = response;
        String[] listData = new String[response.features.length];
        for (int i = 0; i < response.features.length; i++) {
            Feature feature = response.features[i];
            listData[i] = feature.properties.mag + " " + feature.properties.place;
        }
        jlist.setListData(listData);
    }

}

package com.ipn.mx.controladores;

import com.ipn.mx.serial.ControladorSerial;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import jssc.SerialPort;
import jssc.SerialPortException;

/**
 *
 * @author Edgar Miranda
 */
public class FXMLDocumentController implements Initializable {

    private List<Byte> flujoBytes = new ArrayList<>();
    private ControladorSerial cs = new ControladorSerial();
    private SerialPort puertoDestino;
    private SerialPort puertoLectura;

    @FXML
    private Label labelResultado;
    @FXML
    private ComboBox<Integer> tf_RPM;
    @FXML
    private TextField tf_Secuencia;
    @FXML
    private Button botonTransferir;
    @FXML
    private ComboBox<String> menu_Direccion;
    @FXML
    private ComboBox<String> comboBoxPuertosDisponibles;
    @FXML
    private Label labelPuertoElegido;
    @FXML
    private Button botonLeerPuerto;
    @FXML
    private Button botonPuertosDisponibles;
    @FXML
    private Label puertosEstado;
    @FXML
    private Label labelDatosLeidos;
    @FXML
    private ComboBox<String> comboBoxPuertosDisponiblesEscuchar;
    @FXML
    private Label escuchandoPuertoLabel;
    @FXML
    private Label datosAEnviar;
    @FXML
    private Label textoTitulo;

    @FXML
    private void enviarDatosAPuerto(ActionEvent event) {
        int porcentajeVelocidad = tf_RPM.getValue();
        String direccion = menu_Direccion.getValue();
        System.out.println("\nEntra: " + muxDireccion(direccion) + " " + muxVelocidad(porcentajeVelocidad));
        flujoBytes.add(muxDireccion(direccion));
        flujoBytes.add(muxVelocidad(porcentajeVelocidad));
        byte[] datos = new byte[flujoBytes.size()];
        int i = 0;
        for (byte elementoFlujo : flujoBytes) {
            datos[i] = elementoFlujo;
            i++;
        }
        datosAEnviar.setText("Dirección en byte: " + muxDireccion(direccion)
                + "\n% Velocidad en byte: " + muxVelocidad(porcentajeVelocidad));
        //Ejemplo forzado, la lista se puede desplegar en la GUI
        /*cs.ecribirEnPuerto(puertoDestino, datos);*/
    }

    @FXML
    private void identificarPuertos(ActionEvent event) {
        ObservableList<String> items = FXCollections.observableArrayList();
        String[] puertos = cs.obtenerPuertosDisponibles();
        if (puertos.length != 0) {
            puertosEstado.setText("Puertos diponibles");
            for (int i = 0; i < cs.obtenerPuertosDisponibles().length; i++) {
                items.add(cs.obtenerPuertosDisponibles()[i]);
            }
            comboBoxPuertosDisponibles.setItems(items);
            comboBoxPuertosDisponiblesEscuchar.setItems(items);
            comboBoxPuertosDisponibles.setDisable(false);
            comboBoxPuertosDisponiblesEscuchar.setDisable(false);
        } else {
            puertosEstado.setText("No se encontraron puertos");
            comboBoxPuertosDisponibles.setDisable(true);
            comboBoxPuertosDisponiblesEscuchar.setDisable(true);
        }
    }

    @FXML
    public void leerDatosDePuertoSeleccionado() throws SerialPortException {
        puertoLectura.openPort();
        System.out.println("\nCTS: " + puertoLectura.isCTS() + " DSR: " + puertoLectura.isDSR());
        byte[] datosRecibidos = cs.leerDePuerto(puertoLectura);
        String s = "";
        if (datosRecibidos != null) {
            for (int i = 0; i < datosRecibidos.length - 1; i++) {
                System.out.println("\n" + (byte)datosRecibidos[i]);
                s.concat(String.valueOf(datosRecibidos[i]) + " ");
            }
            labelDatosLeidos.setAlignment(Pos.CENTER);
            labelDatosLeidos.setText(s);
        } else {
            labelDatosLeidos.setAlignment(Pos.CENTER);
            labelDatosLeidos.setText("Puerto ocupado o cerrado");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        puertosEstado.setAlignment(Pos.CENTER);
        labelPuertoElegido.setAlignment(Pos.CENTER);
        llenarComboDireccion();
        llenarComboVelocidad();
        comboBoxPuertosDisponibles.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                puertoDestino = cs.crearInstanciaPuertoConNombre(newValue);
                labelPuertoElegido.setText(newValue);
            }
        });
        comboBoxPuertosDisponiblesEscuchar.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                puertoLectura = cs.crearInstanciaPuertoConNombre(newValue);
                escuchandoPuertoLabel.setText(newValue);
            }
        });
    }

    public void llenarComboDireccion() {
        ObservableList<String> items = FXCollections.observableArrayList();
        items.addAll("Izquierda", "Derecha");
        menu_Direccion.setItems(items);
    }

    public void llenarComboVelocidad() {
        ObservableList<Integer> items = FXCollections.observableArrayList();
        List<Integer> arregloPorcentaje = new ArrayList<>();
        for (int i = 0; i <= 100; i++) {
            arregloPorcentaje.add(i);
        }
        items.addAll(arregloPorcentaje);
        tf_RPM.setItems(items);
    }

    public byte muxDireccion(String eleccion) {
        switch (eleccion) {
            case "Derecha":
                return (byte) 0xFF;
            case "Izquierda":
                return (byte) 0x00;
            default:
                return 0x01;
        }
    }

    public byte muxVelocidad(int porcentaje) {
        int inicial = 0x00 + porcentaje;
        return (byte) inicial;
    }

//    public static int convert(int n) {
//        return Integer.valueOf(String.valueOf(n), 16);
//    }
//
//    public static byte[] hexStringToByteArray(String s) {
//        int len = s.length();
//        byte[] data = new byte[len / 2];
//        for (int i = 0; i < len; i += 2) {
//            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
//                    + Character.digit(s.charAt(i + 1), 16));
//        }
//        return data;
//    }
}

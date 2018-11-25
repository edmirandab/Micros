/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ipn.mx.controladores;

import com.ipn.mx.serial.ControladorSerial;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 *
 * @author Edgar Miranda
 */
public class FXMLDocumentController implements Initializable {

    List<Byte> flujoBytes = new ArrayList<>();
    ControladorSerial cs = new ControladorSerial();

    @FXML
    private Label labelResultado;
    @FXML
    private Label label;
    @FXML
    private ComboBox<Integer> tf_RPM;
    @FXML
    private TextField tf_RPS;
    @FXML
    private TextField tf_Hz;
    @FXML
    private TextField tf_Secuencia;
    @FXML
    private Button botonTransferir;
    @FXML
    private ComboBox<String> menu_Direccion;

    @FXML
    private void handleButtonAction(ActionEvent event) {
        int porcentajeVelocidad = tf_RPM.getValue();
        String direccion = menu_Direccion.getValue();
//        labelResultado.setText("Velocidad en byte: " + muxDireccion(direccion));
//        System.out.println("\nDirección (byte): " + muxDireccion(direccion)
//                + ", Velocidad (byte): " + muxVelocidad(porcentajeVelocidad));
        flujoBytes.add(muxDireccion(direccion));
        flujoBytes.add(muxVelocidad(porcentajeVelocidad));
        byte[] datos = new byte[flujoBytes.size()-1];
        int i = 0;
        for (byte elementoFlujo : flujoBytes) {
            datos[i] = elementoFlujo;
            i++;
        }
        String[]puertosEncontrados = cs.obtenerPuertosDisponibles();
        //Ejemplo forzado, la lista se puede desplegar en la GUI
        cs.ecribirEnPuerto(puertosEncontrados[0],datos);
        //Aquí después se cerrará el puerto (?)
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        llenarComboDireccion();
        llenarComboVelocidad();
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
    
    public void pruebaBranch(){
        System.out.println("hola");
        
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

package com.ipn.mx.serial;

import jssc.SerialPortList;
import jssc.SerialPort;
import jssc.SerialPortException;

/**
 *
 * @author Edgar Miranda
 */
public class ControladorSerial {

    public String[] obtenerPuertosDisponibles() {
        return SerialPortList.getPortNames();
    }

    public void ecribirEnPuerto(SerialPort puerto, byte[] datos) {
        try {
            puerto.openPort(); //Open serial port
            puerto.setParams(9600, 8, 1, 0); //Set params.
            puerto.writeBytes("This is a test string".getBytes());//Write data to port
            puerto.closePort();//Close serial port
        } catch (SerialPortException ex) {
            System.out.println(ex);
        }
    }

    public byte[] leerDePuerto(SerialPort puerto) {
        try {
            puerto.openPort(); //Open serial port
            puerto.setParams(9600, 8, 1, 0); //Set params.
            byte[] buffer = puerto.readBytes(10); //Read 10 bytes from serial port
            puerto.closePort(); //Close serial port
            return buffer;
        } catch (SerialPortException ex) {
            System.out.println(ex);
            return null;
        }
    }

    public SerialPort crearInstanciaPuertoConNombre(String nombre) {
        return new SerialPort(nombre);
    }

}

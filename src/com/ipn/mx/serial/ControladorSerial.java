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

    public void ecribirEnPuerto(String nombrePuerto, byte[]datos) {
        SerialPort serialPort = new SerialPort(nombrePuerto);
        try {
            serialPort.openPort(); //Open serial port
            serialPort.setParams(9600, 8, 1, 0); //Set params.
            serialPort.writeBytes("This is a test string".getBytes());//Write data to port
            serialPort.closePort();//Close serial port
        } catch (SerialPortException ex) {
            System.out.println(ex);
        }
    }

}

package com.ip.hydra;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class UdpScanner {

    public static boolean isUdpPortOpen(InetAddress ipAddress, int port, int timeOut) {
        // Создаём массив байтов для перебора портов
        byte[] payload = new byte[] {
            (byte) ((port >> 8) & 0xFF), (byte) (port & 0xFF)
        };

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(timeOut);

            // Формируем и отправляем пакет на конкретный порт
            DatagramPacket packet = new DatagramPacket(payload, payload.length, ipAddress, port);
            socket.send(packet);

            // Ожидаем ответ и анализируем его
            DatagramPacket responsePacket = new DatagramPacket(new byte[1024], 1024);
            socket.receive(responsePacket);
            return true; // Если ответ есть – порт открыт
        } catch (SocketTimeoutException e) {
            return false; // Таймаут – порт закрыт
        } catch (SocketException e) {
            return false; // Проблемы с сокетом – порт закрыт
        } catch (IOException e) {
            return false; // Проблемы с вводом/выводом – считаем порт закрытым
        }
    }

    public static void main(String[] args) {
        try {
            InetAddress targetIp = InetAddress.getByName("127.0.0.1"); // Укажите нужный IP-адрес
            int timeout = 1000; // Таймаут в миллисекундах

            // Сканируем диапазон портов от 0 до 65535
            for (int port = 0; port <= 65535; port++) {
                if (isUdpPortOpen(targetIp, port, timeout)) {
                    System.out.println("UDP порт " + port + " открыт");
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
    }
}
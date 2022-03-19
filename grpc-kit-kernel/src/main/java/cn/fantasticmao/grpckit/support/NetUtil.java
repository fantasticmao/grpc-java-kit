package cn.fantasticmao.grpckit.support;

import cn.fantasticmao.grpckit.GrpcKitConfig;

import java.io.IOException;
import java.net.*;
import java.util.*;

/**
 * A class related to {@link java.net.NetworkInterface} and {@link java.net.InetAddress}.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-03-18
 */
public class NetUtil {
    private static final Boolean PREFER_IPV6_ADDRESSES = Boolean.getBoolean("java.net.preferIPv6Addresses");

    public static InetAddress getLocalAddress() throws SocketException, UnknownHostException {
        List<NetworkInterface> validNetworkInterfaces = getValidNetworkInterfaces();
        for (NetworkInterface networkInterface : validNetworkInterfaces) {
            Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress address = addresses.nextElement();
                if (address == null || address.isLoopbackAddress()) {
                    continue;
                }
                if (address instanceof Inet6Address && !PREFER_IPV6_ADDRESSES) {
                    continue;
                }
                try {
                    if (!address.isReachable(100)) {
                        continue;
                    }
                } catch (IOException ignored) {
                    continue;
                }
                return address;
            }
        }
        throw new SocketException("Failed to found valid Network Interfaces");
    }

    private static List<NetworkInterface> getValidNetworkInterfaces() throws SocketException {
        List<NetworkInterface> validNetworkInterfaces = new LinkedList<>();
        // get valid Network Interfaces
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaces.nextElement();
            if (networkInterface == null || networkInterface.isLoopback()
                || networkInterface.isVirtual() || !networkInterface.isUp()) {
                continue;
            }
            validNetworkInterfaces.add(networkInterface);
        }

        // pick the prefer Network Interface
        String preferInterface = GrpcKitConfig.getInstance().getGrpc().getServer().getInterfaceName();
        if (preferInterface != null) {
            for (NetworkInterface networkInterface : validNetworkInterfaces) {
                if (Objects.equals(networkInterface.getDisplayName(), preferInterface)) {
                    validNetworkInterfaces = Collections.singletonList(networkInterface);
                }
            }
        }
        return validNetworkInterfaces;
    }

}
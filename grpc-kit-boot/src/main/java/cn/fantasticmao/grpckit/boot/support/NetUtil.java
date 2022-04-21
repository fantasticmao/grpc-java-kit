package cn.fantasticmao.grpckit.boot.support;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.*;
import java.util.*;

/**
 * An util class for {@link java.net.NetworkInterface NetworkInterface} and {@link java.net.InetAddress InetAddress}.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-03-18
 */
public interface NetUtil {
    Boolean PREFER_IPV6_ADDRESSES = Boolean.getBoolean("java.net.preferIPv6Addresses");

    static InetAddress getLocalAddress(@Nullable String preferInterface) throws SocketException, UnknownHostException {
        List<NetworkInterface> validNetworkInterfaces = getValidNetworkInterfaces(preferInterface);
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

    static List<NetworkInterface> getValidNetworkInterfaces(@Nullable String preferInterface) throws SocketException {
        List<NetworkInterface> validNetworkInterfaces = new LinkedList<>();
        // get valid Network Interfaces.
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaces.nextElement();
            if (networkInterface == null || networkInterface.isLoopback()
                || networkInterface.isVirtual() || !networkInterface.isUp()) {
                continue;
            }
            validNetworkInterfaces.add(networkInterface);
        }

        // pick the favor Network Interface.
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

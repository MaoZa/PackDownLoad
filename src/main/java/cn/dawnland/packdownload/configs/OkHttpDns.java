package cn.dawnland.packdownload.configs;

import okhttp3.Dns;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

public class OkHttpDns implements Dns {
    @Override
    public List<InetAddress> lookup(String hostname) throws UnknownHostException {
        try {
            List<InetAddress> list = Dns.SYSTEM.lookup(hostname);
            return list;
        } catch (Exception e) {
            throw e;
        }
    }
}

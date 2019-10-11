import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

/**
 * @author hradecky
 */
public class Demonstration {

    public static void main(String[] args) throws UnknownHostException {

        int userCount = 15000000;
        int ipCount = 10000;

        List<User> users = new ArrayList<>();
        for (int i = 0; i < userCount; i++) users.add(new User("user #" + i));
        ConsistentHashing<User, Ip> consistentHashing = new ConsistentHashing<>();

        for (int i = 0; i < ipCount; i++) consistentHashing.addValue(new Ip(intToIp(i)));


        Map<User, Ip> map = users.stream()
                .collect(toMap(Function.identity(), consistentHashing::getKeyValue));

        consistentHashing.addValue(new Ip(intToIp(ipCount + 1)));


        Map<User, Ip> mapAfterIpAdded = users.stream()
                .collect(toMap(Function.identity(), consistentHashing::getKeyValue));

        assert map.keySet().equals(mapAfterIpAdded.keySet());
        MapDifference<User, Ip> diff = Maps.difference(map, mapAfterIpAdded);

        System.out.println(String.format("%.4f", 100 - diff.entriesDiffering().keySet().size() / (float) userCount * 100) + "% unchanged");

    }

    static class User {
        public final String userId;

        public User(String userId) {
            this.userId = userId;
        }
    }

    static class Ip {
        public final String ip;

        public Ip(String ip) {
            this.ip = ip;
        }
    }

    private static String intToIp(int value) throws UnknownHostException {
        byte[] b = new byte[4];
        b[0] = (byte) ((value & 0xFF000000) >> 24);
        b[1] = (byte) ((value & 0x00FF0000) >> 16);
        b[2] = (byte) ((value & 0x0000FF00) >> 8);
        b[3] = (byte) (value & 0x000000FF);
        InetAddress address = InetAddress.getByAddress(b);
        return address.getHostAddress();
    }


}

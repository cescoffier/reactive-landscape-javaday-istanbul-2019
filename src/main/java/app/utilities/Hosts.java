package app.utilities;

public class Hosts {

    public static String getHost() {
        if (System.getenv("CONTAINERIZED") != null) {
            // Special DNS name in Docker 18+ referring to host.
            return "host.docker.internal";
        } else {
            return "localhost";
        }
    }
}

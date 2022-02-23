
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class NetworkNode {

    public static void main(String[] args) throws IOException {
        
        Map<String, Integer> resources = new HashMap<>();
        String gateway = null;
        String networkIdentifier = null;
        String command = null;
        int tcpPort = 0;

        // Parameter scan loop
        for (int i = 0; i < args.length; i++) {

            switch (args[i]) {
                case "-ident":
                    Random random = new Random();
                    networkIdentifier = args[++i] + (random.nextInt(900) + 100);
                    break;
                case "-gateway":
                    String[] gatewayArray = args[++i].split(":");
                    gateway = gatewayArray[0];
                    break;
                case "-tcpport":
                    tcpPort = Integer.parseInt(args[++i]);
                    break;
                case "terminate":
                    command = "TERMINATE";
                    break;
                default:
                    if (command == null) {
                        command = args[i];
                    } else if (!"TERMINATE".equals(command)) {
                        command += " " + args[i];
                    }
            }

        }

        if (command != null) {

            String[] allocatedResources = command.split(" ");

            for (String allocatedResource : allocatedResources) {

                String[] tokens = allocatedResource.split(":");
                String resourceKey = tokens[0];
                Integer resourceValue = Integer.parseInt(tokens[1]);
                resources.put(resourceKey, resourceValue);

            }

            System.out.println();
            System.out.println("The following resources have been allocated to this network node:");
            System.out.println(resources.toString());

            Network network = new Network(tcpPort, resources);
            network.start();
        }
    }
    
}

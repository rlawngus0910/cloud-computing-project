import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.*;

import java.util.Scanner;

public class AWSProject {

    static AmazonEC2 ec2;

    private static void init() throws Exception {

        ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();

        try {
            credentialsProvider.getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +
                            "Please make sure that your credentials file is at the correct " +
                            "location (~/.aws/credentials), and is in valid format.",
                    e);
        }

        ec2 = AmazonEC2ClientBuilder.standard()
                .withCredentials(credentialsProvider)
                .withRegion("us-east-2")
                .build();
    }
    public static void main(String[] args) throws Exception {

        init();

        Scanner menu = new Scanner(System.in);
        Scanner id_string = new Scanner(System.in);
        int number = 0;

        while(true) {
            System.out.println("                                                            ");
            System.out.println("                                                            ");
            System.out.println("------------------------------------------------------------");
            System.out.println("           Amazon AWS Control Panel using SDK               ");
            System.out.println("                                                            ");
            System.out.println("  Cloud Computing, Computer Science Department              ");
            System.out.println("                           at Chungbuk National University  ");
            System.out.println("------------------------------------------------------------");
            System.out.println("  1. list instance                2. available zones         ");
            System.out.println("  3. start instance               4. available regions      ");
            System.out.println("  5. stop instance                6. create instance        ");
            System.out.println("  7. reboot instance              8. list images            ");
            System.out.println("                                 99. quit                   ");
            System.out.println("------------------------------------------------------------");

            System.out.print("Enter an integer: ");

            number = menu.nextInt();

            switch (number) {
                case 1:
                    listInstances();
                    break;

                case 2:
                    availableZones();
                    break;

                case 3:
                    startInstances();
                    break;

                case 4:
                    availableRegions();
                    break;

                case 5:
                    stopInstances();
                    break;

                case 6:
                    createInstances();
                    break;

                case 7:
                    rebootInstances();
                    break;

                case 8:
                    listImages();
                    break;

                case 99:
                    return;
            }
        }
    }

    public static void listInstances() {
        System.out.println("Listing instances....");
        boolean done = false;

        DescribeInstancesRequest request = new DescribeInstancesRequest();

        while(!done) {
            DescribeInstancesResult response = ec2.describeInstances(request);

            for (Reservation reservation : response.getReservations()) {
                for (Instance instance : reservation.getInstances()) {
                    System.out.printf(
                            "[id] %s, " +
                            "[AMI] %s, " +
                            "[type] %s, " +
                            "[state] %10s, " +
                            "[monitoring state] %s",
                            instance.getInstanceId(),
                            instance.getImageId(),
                            instance.getInstanceType(),
                            instance.getState().getName(),
                            instance.getMonitoring().getState());
                }
                System.out.println();
            }

            request.setNextToken(response.getNextToken());

            if(response.getNextToken() == null) {
                done = true;
            }
        }
    }

    public static void availableZones() {
        DescribeAvailabilityZonesResult zonesResult = ec2.describeAvailabilityZones();
        int zoneSize = 0;

        System.out.println("Available zones....");
        for (AvailabilityZone zone : zonesResult.getAvailabilityZones()) {
            System.out.printf(
                    "[id] %s " +
                    "[region] %s " +
                    "[zone] %s\n",
                    zone.getZoneId(),
                    zone.getRegionName(),
                    zone.getZoneName());

            zoneSize++;
        }
        System.out.printf("You have access to %d Availability Zones.\n", zoneSize);
    }

    public static void startInstances() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("id : ");
        String instance_id = scanner.nextLine();

        StartInstancesRequest request = new StartInstancesRequest().withInstanceIds(instance_id);

        ec2.startInstances(request);
    }

    public static void availableRegions() {
        DescribeRegionsResult regionsResult = ec2.describeRegions();

        for(Region region : regionsResult.getRegions()) {
            System.out.printf(
                    "[id] %s " +
                    "[endpoint] %s\n",
                    region.getRegionName(),
                    region.getEndpoint());

        }
    }

    public static void stopInstances() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("id : ");
        String instance_id = scanner.nextLine();

        StopInstancesRequest request = new StopInstancesRequest().withInstanceIds(instance_id);

        ec2.stopInstances(request);
    }

    public static void createInstances() {

    }

    public static void rebootInstances() {

    }

    public static void listImages() {

    }
}

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
            System.out.println("  9. terminate instance          10. create images          ");
            System.out.println(" 11. delete images               99. quit                   ");
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

                case 9:
                    terminateInstances();
                    break;

                case 10:
                    createImages();
                    break;

                case 11:
                    deleteImages();
                    break;

                case 99:
                    System.out.println("Exit the program. Bye.");
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
        System.out.print("Enter instance id : ");
        String instance_id = scanner.nextLine();

        try {
            StartInstancesRequest request = new StartInstancesRequest().withInstanceIds(instance_id);
            ec2.startInstances(request);

            System.out.printf("Successfully started instance %s\n", instance_id);
        } catch (Exception e) {
            System.out.println("Can't find this instance.");
        }
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
        System.out.print("Enter instance id : ");
        String instance_id = scanner.nextLine();

        try {
            StopInstancesRequest request = new StopInstancesRequest().withInstanceIds(instance_id);
            ec2.stopInstances(request);

            System.out.printf("Successfully stopped instance %s\n", instance_id);
        } catch (Exception e) {
            System.out.println("Can't find this instance.");
        }
    }

    public static void createInstances() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter ami id : ");
        String ami_id = scanner.nextLine();

        try {
            RunInstancesRequest runRequest = new RunInstancesRequest()
                    .withImageId(ami_id)
                    .withInstanceType(InstanceType.T2Micro)
                    .withMaxCount(1)
                    .withMinCount(1);

            RunInstancesResult runResponse = ec2.runInstances(runRequest);
            String reservation_id = runResponse.getReservation().getInstances().get(0).getInstanceId();

            System.out.printf("Successfully started EC2 instance %s based on %s\n", reservation_id, ami_id);
        } catch (Exception e) {
            System.out.println("Can't find this image.");
        }
    }

    public static void rebootInstances() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter instance id : ");
        String instance_id = scanner.nextLine();

        try {
            RebootInstancesRequest request = new RebootInstancesRequest().withInstanceIds(instance_id);
            ec2.rebootInstances(request);

            System.out.printf("Successfully rebooted instance %s\n", instance_id);

        } catch (Exception e) {
            System.out.println("Can't find this instance.");
        }
    }

    public static void listImages() {
        DescribeImagesRequest request = new DescribeImagesRequest().withOwners("self");
        System.out.println("Listing images....");

        DescribeImagesResult response = ec2.describeImages(request);

        for(Image image : response.getImages()) {
            System.out.printf(
                    "[ImageID] %s " +
                    "[Name] %s " +
                    "[Owner] %s\n",
                    image.getImageId(),
                    image.getName(),
                    image.getOwnerId());
        }
    }

    public static void terminateInstances() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter instance id : ");
        String instance_id = scanner.nextLine();

        try {
            TerminateInstancesRequest request = new TerminateInstancesRequest().withInstanceIds(instance_id);
            ec2.terminateInstances(request);

            System.out.printf("Successfully terminated instance %s\n", instance_id);

        } catch (Exception e) {
            System.out.println("Can't find this instance.");
        }
    }

    public static void createImages() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter instance id : ");
        String instance_id = scanner.nextLine();
        System.out.print("Enter image name : ");
        String img_name = scanner.nextLine();

        try {
            CreateImageRequest request = new CreateImageRequest().withInstanceId(instance_id).withName(img_name);
            CreateImageResult response = ec2.createImage(request);

            String ami_id = response.getImageId();
            System.out.printf("Successfully started image %s based on %s\n", ami_id, instance_id);
        } catch (Exception e) {
            System.out.println("Can't create image.");
        }
    }

    public static void deleteImages() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter image id : ");
        String ami_id = scanner.nextLine();

        try {
            DeregisterImageRequest request = new DeregisterImageRequest().withImageId(ami_id);
            ec2.deregisterImage(request);

            System.out.printf("Successfully deleted image %s\n", ami_id);
        } catch (Exception e) {
            System.out.println("Can't create image.");
        }
    }
}

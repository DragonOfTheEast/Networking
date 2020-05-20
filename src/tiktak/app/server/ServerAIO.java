package tiktak.app.server;

public class ServerAIO {
    public static void main(String[] args) {

        if(args.length != 2) {

            System.err.println("Parameter(s): <Port>  <Password File>");

            System.exit(1);

        }

        int serverPort = 0;
        try {
            serverPort = Integer.parseInt(args[0]);
        }
        catch (NumberFormatException e) {
            System.err.println("invalid port format");
            System.exit(1);
        }

    }

}


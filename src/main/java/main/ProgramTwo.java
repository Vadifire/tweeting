package main;

import main.resources.GetTimelineResource;
import twitter4j.Status;

import java.util.List;

public class ProgramTwo {

    public static void main(String args[]) {
        List<Status> statuses = GetTimelineResource.getHomeTimelineStatuses();
        printStatuses(statuses);
    }

    public static void printStatuses(List<Status> statuses) {
        if (statuses == null) {
            return;
        }
        for (Status status : statuses) {
            System.out.println(status.getUser().getName() + ":" +
                    status.getText());
        }
    }

}

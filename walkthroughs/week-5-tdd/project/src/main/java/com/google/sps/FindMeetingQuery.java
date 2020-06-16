// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Collections;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {

    // Holds all possible time slot in which request meeting could occur
    Collection<TimeRange> possibleMeetingRanges = new ArrayList<>();

    // Holds all mandatory attendees for the requested meeting 
    Collection<String> meetingAttendees = request.getAttendees();

    // Holds all optional attendees for the requested meeting 
    Collection<String> optionalAttendees = request.getOptionalAttendees();

    long meetingDuration = request.getDuration();

    // Find existing events on attendee schedule and register their time slots 
    ArrayList<TimeRange> incompatibleMeetingRanges = getIncompatibleMeetingRanges(events, meetingAttendees, optionalAttendees, meetingDuration);

    // Sort all events to compare them from the beginning of the day starting with the earliest
    Collections.sort(incompatibleMeetingRanges, TimeRange.ORDER_BY_START);

    // Start comparison of event at the beginning of the day
    int meetingStartTime = 0;
    
    // Use start and end ranges of exisiting events to compare free slot ranges to meeting duration
    for (TimeRange scheduledTimeRange : incompatibleMeetingRanges) {
      int scheduledStartTime = scheduledTimeRange.start();
      int scheduledEndTime = scheduledTimeRange.end();
      if (meetingStartTime + meetingDuration <= scheduledStartTime) {
        possibleMeetingRanges.add(TimeRange.fromStartEnd(meetingStartTime, scheduledStartTime, false));
      }

      // Check for overlapping of events
      if (meetingStartTime < scheduledEndTime) {
        meetingStartTime = scheduledEndTime;
      }
      
    }

    int totalMinutesInDay = 24 * 60;

    // Ensure that remaining time in day after all events for the day have been checked is added to the time ranges
    if (meetingStartTime + meetingDuration <= totalMinutesInDay) {
      possibleMeetingRanges.add(TimeRange.fromStartEnd(meetingStartTime, totalMinutesInDay, false));
    }

    return possibleMeetingRanges;
  }

  public ArrayList<TimeRange> getIncompatibleMeetingRanges(Collection<Event> events, Collection<String> meetingAttendees, Collection<String> optionalAttendees, long meetingDuration) {
    // Identify filled time slots by comparing attendes of events with meeting attendes
    ArrayList<TimeRange> incompatibleMeetingRanges = new ArrayList<TimeRange>();
    
    for (Event existingEvent : events) {
      TimeRange eventRange = existingEvent.getWhen();

      // Check for mandatory attendees who have events scheduled that could affect meeting times
      if (!(Collections.disjoint(existingEvent.getAttendees(), meetingAttendees))) {
        incompatibleMeetingRanges.add(eventRange);
      }

      // Check for optional attendees who have events scheduled that could affect meeting times and make them unavailable
      if ((!(Collections.disjoint(existingEvent.getAttendees(), optionalAttendees))) &&  (24*60 - existingEvent.getWhen().duration() > meetingDuration) && (existingEvent.getWhen().duration() >= meetingDuration)) {
        incompatibleMeetingRanges.add(eventRange);
      }
  
    } 

    return incompatibleMeetingRanges;
  }
}

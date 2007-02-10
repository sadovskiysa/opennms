/*
 * Copyright 2006 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.opennms.netmgt.dashboard.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.opennms.netmgt.dashboard.client.Person;
import org.opennms.netmgt.dashboard.client.Professor;
import org.opennms.netmgt.dashboard.client.Schedule;
import org.opennms.netmgt.dashboard.client.SchoolCalendarService;
import org.opennms.netmgt.dashboard.client.Student;
import org.opennms.netmgt.dashboard.client.TimeSlot;

public class SchoolCalendarServiceImpl extends RemoteServiceServlet implements
    SchoolCalendarService {

  private static final String[] FIRST_NAMES = new String[]{
    "Mike", "Jonathan", "Tarus", "David", "Johan", "Jeff", "Bill", "Antonio",
    "Craig", "Matt"};

  private static final String[] LAST_NAMES = new String[]{
    "Huot", "Sartin", "Balog", "Hustace", "Edstrom", "Gehlbach", "Ayres", "Russo",
    "Miskel", "Brozowski" };

  private static final String[] SUBJECTS = new String[]{
    "Prestidigitation", "Russian Literature", "Great Feats of IT Magic", "Underwater Basket Weaving",
    "Springology", "Computer Science", "Wicketology", "Materials Engineering",
    "English Literature", "Geology"};

  private static final Person[] NO_PEOPLE = new Person[0];

  public SchoolCalendarServiceImpl() {
    generateRandomPeople();
  }

  public Person[] getPeople(int startIndex, int maxCount) {
    int peopleCount = people.size();

    int start = startIndex;
    if (start >= peopleCount) {
      return NO_PEOPLE;
    }

    int end = Math.min(startIndex + maxCount, peopleCount);
    if (start == end) {
      return NO_PEOPLE;
    }

    int resultCount = end - start;
    Person[] results = new Person[resultCount];
    for (int from = start, to = 0; to < resultCount; ++from, ++to) {
      results[to] = (Person) people.get(from);
    }

    return results;
  }

  private void generateRandomPeople() {
    for (int i = 0; i < MAX_PEOPLE; ++i) {
      Person person = generateRandomPerson();
      people.add(person);
    }
  }

  private Person generateRandomPerson() {
    // 1 out of every so many people is a prof.
    //
    if (rnd.nextInt(STUDENTS_PER_PROF) == 1) {
      return generateRandomProfessor();
    } else {
      return generateRandomStudent();
    }
  }

  private Person generateRandomProfessor() {
    Professor prof = new Professor();

    String firstName = pickRandomString(FIRST_NAMES);
    String lastName = pickRandomString(LAST_NAMES);
    prof.setName("Dr. " + firstName + " " + lastName);

    String subject = pickRandomString(SUBJECTS);
    prof.setDescription("Professor of " + subject);

    generateRandomSchedule(prof.getTeachingSchedule());

    return prof;
  }

  private void generateRandomSchedule(Schedule sched) {
    int range = MAX_SCHED_ENTRIES - MIN_SCHED_ENTRIES + 1;
    int howMany = MIN_SCHED_ENTRIES + rnd.nextInt(range);

    TimeSlot[] timeSlots = new TimeSlot[howMany];

    for (int i = 0; i < howMany; ++i) {
      int startHrs = 8 + rnd.nextInt(9); // 8 am - 5 pm
      int startMins = 15 * rnd.nextInt(4); // on the hour or some quarter
      int dayOfWeek = 1 + rnd.nextInt(5); // Mon - Fri

      int absStartMins = 60 * startHrs + startMins; // convert to minutes
      int absStopMins = absStartMins + CLASS_LENGTH_MINS;

      timeSlots[i] = new TimeSlot(dayOfWeek, absStartMins, absStopMins);
    }

    Arrays.sort(timeSlots);

    for (int i = 0; i < howMany; ++i) {
      sched.addTimeSlot(timeSlots[i]);
    }
  }

  private Person generateRandomStudent() {
    Student student = new Student();

    String firstName = pickRandomString(FIRST_NAMES);
    String lastName = pickRandomString(LAST_NAMES);
    student.setName(firstName + " " + lastName);

    String subject = pickRandomString(SUBJECTS);
    student.setDescription("Majoring in " + subject);

    generateRandomSchedule(student.getClassSchedule());

    return student;
  }

  private String pickRandomString(String[] a) {
    int i = rnd.nextInt(a.length);
    return a[i];
  }

  /**
   * Write the serialized response out to stdout. This is a very unusual thing
   * to do, but it allows us to create a static file version of the response
   * without deploying a servlet.
   */
  protected void onAfterResponseSerialized(String serializedResponse) {
    System.out.println(serializedResponse);
  }

  private final List people = new ArrayList();
  private final Random rnd = new Random(3);
  private static final int CLASS_LENGTH_MINS = 50;
  private static final int MAX_SCHED_ENTRIES = 5;
  private static final int MIN_SCHED_ENTRIES = 1;
  private static final int MAX_PEOPLE = 100;
  private static final int STUDENTS_PER_PROF = 5;
}

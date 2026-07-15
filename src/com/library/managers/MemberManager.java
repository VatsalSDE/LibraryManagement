package com.library.managers;

import com.library.exceptions.InvalidFineException;

import com.library.models.Member;

import com.library.exceptions.MemberNotFoundException;

import com.library.exceptions.InvalidMemberException;

import com.library.utilities.CsvUtils;

import java.io.*;

import java.util.List;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class MemberManager {

    // Shared across client-handler threads on the server.
    private ConcurrentHashMap<String, Member> members;

    public MemberManager() {

        this.members = new ConcurrentHashMap<>();

    }

    public String addMember(String name, String email, String phoneNumber) throws InvalidMemberException {

        if (!isValidEmail(email)) {

            throw new InvalidMemberException("Invalid email format");

        }

        if (!isValidPhone(phoneNumber)) {

            throw new InvalidMemberException("Invalid phone number (must be 10 digits)");

        }

        for (Member member : members.values()) {

            if (member.getEmail().equalsIgnoreCase(email)) {

                throw new InvalidMemberException("Email already exists");

            }

            if (member.getPhoneNumber().equals(phoneNumber)) {

                throw new InvalidMemberException("Phone number already exists");

            }

        }

        Member member = new Member(name, email, phoneNumber);

        members.put(member.getMemberId(), member);

        return member.toString();

    }

    public void removeMember(String memberId) throws MemberNotFoundException {

        if (!members.containsKey(memberId)) {

            throw new MemberNotFoundException("Member not found");

        }

        members.remove(memberId);

    }

    public Member getMemberById(String memberId) throws MemberNotFoundException {

        if (!members.containsKey(memberId)) {

            throw new MemberNotFoundException("Member not found");

        }

        return members.get(memberId);

    }

    public void updateDue(String memberId, double fineAmount) throws MemberNotFoundException, InvalidFineException {

        if (fineAmount < 0) {

            throw new InvalidFineException("Fine amount cannot be negative");

        }

        Member member = getMemberById(memberId);

        member.setDueAmount(member.getDueAmount() + fineAmount);

    }

    public void clearDue(String memberId) throws MemberNotFoundException {

        Member member = getMemberById(memberId);

        member.setDueAmount(0);

    }

    public boolean canBorrow(String memberId) throws MemberNotFoundException {

        Member member = getMemberById(memberId);

        return member.isDueCleared();

    }

    public List<Member> getAllMembers() {

        return new ArrayList<>(members.values());

    }

    private boolean isValidEmail(String email) {

        return email.contains("@") && email.contains(".");

    }

    private boolean isValidPhone(String phoneNumber) {

        return phoneNumber.matches("\\d{10}");

    }

    // add to the file

    public void saveToFile(String filename) throws IOException {

        FileWriter fw = new FileWriter(filename);

        for (Member member : members.values()) {

            String line = CsvUtils.toCsvLine(member.getMemberId(), member.getName(), member.getEmail(),

                    member.getPhoneNumber(), String.valueOf(member.getDueAmount()));

            fw.write(line + "\n");

        }

        fw.close();

        System.out.println("Members saved to " + filename);

    }

    public void loadFromFile(String filename) throws IOException {

        File file = new File(filename);

        if (!file.exists()) {

            System.out.println("No existing members file found. Starting fresh.");

            return;

        }

        BufferedReader br = new BufferedReader(new FileReader(filename));

        String line;

        while ((line = br.readLine()) != null) {

            List<String> parts = CsvUtils.parseLine(line);

            if (parts.size() < 5) continue;

            String memberId = parts.get(0);

            String name = parts.get(1);

            String email = parts.get(2);

            String phoneNumber = parts.get(3);

            double dueAmount = Double.parseDouble(parts.get(4));

            Member member = new Member(memberId , name, email, phoneNumber , dueAmount);

            members.put(memberId, member);

        }

        br.close();

        System.out.println("Members loaded from " + filename);

    }

}

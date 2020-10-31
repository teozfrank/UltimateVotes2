package net.teozfrank.ultimatevotes.rewards;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import net.teozfrank.ultimatevotes.main.UltimateVotes;
import net.teozfrank.ultimatevotes.util.RewardsManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class CumulativeRewardsTest {

    private UltimateVotes plugin;
    private ServerMock server;
    private RewardsManager rw;

    //First three test scenarios test the following.
    //testScenario1. Player votes 5 times and claims all rewards, result is checking cumulative rewards for 1,2,3,4,5
    //testScenario2. Player votes 2 more times and claims 1 reward, result is checking cumulative rewards for 6
    //testScenario3. Player still has one reward left and claims that, result is checking cumulative rewards for 7
    //testScenario4. Player votes 11 more times, claims 3 rewards, result is checking cumulative rewards for 8,9,10
    //testScenario5. Player claims 8 rewards, result is checking cumulative rewards for 11,12,13,14,15,16,17,18
    //testScenario6. Player votes 4 times claims all rewards, result is checking cumulative rewards for 19,20,21,22

    @Before
    public void setUp() {
        server = MockBukkit.mock();
        plugin = (UltimateVotes) MockBukkit.load(UltimateVotes.class);
        rw = plugin.getRewardsManager();
    }

    @Test
    public void testScenario1() {
        System.out.println("First test started.");
        int votes = 5;
        int unclaimedVotes = 5;
        int claimAmount = -1;
        System.out.println("Votes: " + votes);
        System.out.println("Unclaimed Votes: " + unclaimedVotes);
        System.out.println("Claim Amount: " + claimAmount);
        List<Integer> actuals;
        List<Integer> expected = new ArrayList<>();
        expected.add(1);
        expected.add(2);
        expected.add(3);
        expected.add(4);
        expected.add(5);

        actuals = rw.getCumulativeRewardListOfRewardsToCheckFor(votes, unclaimedVotes, claimAmount);

        System.out.println("Expected");
        for(int expectedInt: expected) {
            System.out.println(expectedInt);
        }
        System.out.println("Actuals");
        for(int actualsInt: actuals) {
            System.out.println(actualsInt);
        }
        Assert.assertArrayEquals(expected.toArray(), actuals.toArray());
    }

    @Test
    public void testScenario2() {
        System.out.println("Second test started.");
        int votes = 7;
        int unclaimedVotes = 2;
        int claimAmount = 1;
        System.out.println("Votes: " + votes);
        System.out.println("Unclaimed Votes: " + unclaimedVotes);
        System.out.println("Claim Amount: " + claimAmount);
        List<Integer> actuals;
        List<Integer> expected = new ArrayList<>();
        expected.add(6);

        actuals = rw.getCumulativeRewardListOfRewardsToCheckFor(votes, unclaimedVotes, claimAmount);

        System.out.println("Expected");
        for(int expectedInt: expected) {
            System.out.println(expectedInt);
        }
        System.out.println("Actuals");
        for(int actualsInt: actuals) {
            System.out.println(actualsInt);
        }
        Assert.assertArrayEquals(expected.toArray(), actuals.toArray());
    }

    /**
     *
     */
    @Test
    public void testScenario3() {
        System.out.println("Third test started.");
        int votes = 7;
        int unclaimedVotes = 1;
        int claimAmount = 1;
        System.out.println("Votes: " + votes);
        System.out.println("Unclaimed Votes: " + unclaimedVotes);
        System.out.println("Claim Amount: " + claimAmount);
        List<Integer> actuals;
        List<Integer> expected = new ArrayList<>();
        expected.add(7);

        actuals = rw.getCumulativeRewardListOfRewardsToCheckFor(votes, unclaimedVotes, claimAmount);

        System.out.println("Expected");
        for(int expectedInt: expected) {
            System.out.println(expectedInt);
        }
        System.out.println("Actuals");
        for(int actualsInt: actuals) {
            System.out.println(actualsInt);
        }
        Assert.assertArrayEquals(expected.toArray(), actuals.toArray());
    }

    @Test
    public void testScenario4() {
        System.out.println("Fourth test started.");
        int votes = 18;
        int unclaimedVotes = 11;
        int claimAmount = 3;
        System.out.println("Votes: " + votes);
        System.out.println("Unclaimed Votes: " + unclaimedVotes);
        System.out.println("Claim Amount: " + claimAmount);
        List<Integer> actuals;
        List<Integer> expected = new ArrayList<>();
        expected.add(8);
        expected.add(9);
        expected.add(10);

        actuals = rw.getCumulativeRewardListOfRewardsToCheckFor(votes, unclaimedVotes, claimAmount);

        System.out.println("Expected");
        for(int expectedInt: expected) {
            System.out.println(expectedInt);
        }
        System.out.println("Actuals");
        for(int actualsInt: actuals) {
            System.out.println(actualsInt);
        }
        Assert.assertArrayEquals(expected.toArray(), actuals.toArray());
    }

    @Test
    public void testScenario5() {
        System.out.println("Fifth test started.");
        int votes = 18;
        int unclaimedVotes = 8;
        int claimAmount = 8;
        System.out.println("Votes: " + votes);
        System.out.println("Unclaimed Votes: " + unclaimedVotes);
        System.out.println("Claim Amount: " + claimAmount);
        List<Integer> actuals;
        List<Integer> expected = new ArrayList<>();
        expected.add(11);
        expected.add(12);
        expected.add(13);
        expected.add(14);
        expected.add(15);
        expected.add(16);
        expected.add(17);
        expected.add(18);


        actuals = rw.getCumulativeRewardListOfRewardsToCheckFor(votes, unclaimedVotes, claimAmount);

        System.out.println("Expected");
        for(int expectedInt: expected) {
            System.out.println(expectedInt);
        }
        System.out.println("Actuals");
        for(int actualsInt: actuals) {
            System.out.println(actualsInt);
        }
        Assert.assertArrayEquals(expected.toArray(), actuals.toArray());
    }

    @Test
    public void testScenario6() {
        System.out.println("Fifth test started.");
        int votes = 22;
        int unclaimedVotes = 4;
        int claimAmount = -1;
        System.out.println("Votes: " + votes);
        System.out.println("Unclaimed Votes: " + unclaimedVotes);
        System.out.println("Claim Amount: " + claimAmount);
        List<Integer> actuals;
        List<Integer> expected = new ArrayList<>();
        expected.add(19);
        expected.add(20);
        expected.add(21);
        expected.add(22);


        actuals = rw.getCumulativeRewardListOfRewardsToCheckFor(votes, unclaimedVotes, claimAmount);

        System.out.println("Expected");
        for(int expectedInt: expected) {
            System.out.println(expectedInt);
        }
        System.out.println("Actuals");
        for(int actualsInt: actuals) {
            System.out.println(actualsInt);
        }
        Assert.assertArrayEquals(expected.toArray(), actuals.toArray());
    }

    @After
    public void tearDown() {
        MockBukkit.unmock();
    }
}

package analizer.utils.group;

import analizer.utils.Pair;
import analizer.utils.Post;
import analizer.utils.SortedPairs;
import analizer.utils.dictionary.Dictionary;

import java.util.*;

public class Group implements Iterable<Post> {
    private List<Post> postList = new ArrayList<Post>();
    private SortedPairs<Double, Integer> frequencyTable = null;
    private int groupId;

    public Group(int id) {
        this.groupId = id;
    }

    public void add(Post post) {
        if (post != null) {
            postList.add(post);
        }
    }

    public boolean isFrequenciesGenerated() {
        return frequencyTable != null;
    }

    public void updateFrequencies() {
        //There should be more elegant way
        HashMap<Integer, Double> idxToFreq = new HashMap<Integer, Double>();
        int gramsCount = 0;
        for (Post post: postList) {
            for (Integer gramId : post.getNGramIds()) {
                Double freq = idxToFreq.get(gramId);
                if (freq == null) {
                    freq = 0d;
                }
                idxToFreq.put(gramId, ++freq);
                gramsCount++;
            }
        }

        frequencyTable = new SortedPairs<Double, Integer>();
        for (Map.Entry<Integer, Double> entry : idxToFreq.entrySet()) {
            frequencyTable.add(entry.getValue() / gramsCount, entry.getKey());
        }

        frequencyTable.sort();
    }

    public Pair<Double, Integer> getFrequency(int index) {
        return frequencyTable != null ? frequencyTable.getPair(index) : null;
    }

    public String getThemeMessage(int frequentNGramCount) {
        if (frequencyTable == null) {
            updateFrequencies();
        }

        StringBuilder builder = new StringBuilder();
        if (frequentNGramCount > frequencyTable.size()) {
            frequentNGramCount = frequencyTable.size();
        }

        for (int i = 1; i < frequentNGramCount + 1; i++) {
            Integer nGramId = frequencyTable.getPair(frequencyTable.size() - i).second;
            builder.append(Dictionary.getInstance().getNGramById(nGramId).getBase()).append(" ");
        }

        return builder.toString();
    }

    public String getThemeMessage(double frequencyThreshold) {
        if (frequencyTable == null) {
            updateFrequencies();
        }

        StringBuilder builder = new StringBuilder();

        for (Pair<Double, Integer> pair : frequencyTable) {
            if (pair.first > frequencyThreshold) {
                Integer nGramId = pair.second;
                builder.append(Dictionary.getInstance().getNGramById(nGramId).getBase()).append(" ");
            }
        }

        return builder.toString();
    }

    public int uniqueNGramsCount() {
        return frequencyTable != null ? frequencyTable.size() : 0;
    }

    public int size() {
        return postList.size();
    }

    public boolean isEmpty() {
        return postList.isEmpty();
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getGroupId() {
        return groupId;
    }

    @Override
    public Iterator<Post> iterator() {
        return new Iterator<Post>() {
            private Iterator<Post> groupIterator = postList.iterator();

            @Override
            public boolean hasNext() {
                return groupIterator.hasNext();
            }

            @Override
            public Post next() {
                return groupIterator.next();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}

package com.thinkaurelius.faunus.mapreduce.operators;

import com.thinkaurelius.faunus.FaunusVertex;
import com.thinkaurelius.faunus.Tokens;
import com.thinkaurelius.faunus.mapreduce.CounterMap;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class EdgeLabelDistribution {

    public static final String DIRECTION = Tokens.makeNamespace(EdgeLabelDistribution.class) + ".direction";

    public enum Counters {
        EDGES_COUNTED,
        VERTICES_COUNTED
    }

    public static class Map extends Mapper<NullWritable, FaunusVertex, Text, LongWritable> {

        private Direction direction;
        // making use of in-map aggregation/combiner
        private CounterMap<String> map;


        @Override
        public void setup(final Mapper.Context context) throws IOException, InterruptedException {
            this.direction = Direction.valueOf(context.getConfiguration().get(DIRECTION));
            this.map = new CounterMap<String>();
        }

        @Override
        public void map(final NullWritable key, final FaunusVertex value, final Mapper<NullWritable, FaunusVertex, Text, LongWritable>.Context context) throws IOException, InterruptedException {
            // todo: take advantage of list.size()
            long counter = 0;
            for (final Edge edge : value.getEdges(this.direction)) {
                counter++;
                //this.map.incr(edge.getLabel(), 1l);
                context.write(new Text(edge.getLabel()), new LongWritable(1l));
            }
            context.getCounter(Counters.VERTICES_COUNTED).increment(1);
            context.getCounter(Counters.EDGES_COUNTED).increment(counter);


            // protected against memory explosion
            /*if (this.map.size() > 1000) {
                this.cleanup(context);
                this.map.clear();
            }*/
        }

        /*@Override
        public void cleanup(final Mapper<NullWritable, FaunusVertex, Text, LongWritable>.Context context) throws IOException, InterruptedException {
            super.cleanup(context);
            for (final java.util.Map.Entry<String, Long> entry : this.map.entrySet()) {
                context.write(new Text(entry.getKey()), new LongWritable(entry.getValue()));
            }
        }*/
    }

    public static class Reduce extends Reducer<Text, LongWritable, Text, LongWritable> {
        @Override
        public void reduce(final Text key, final Iterable<LongWritable> values, final Reducer<Text, LongWritable, Text, LongWritable>.Context context) throws IOException, InterruptedException {
            long totalNumberOfEdges = 0;
            for (final LongWritable token : values) {
                totalNumberOfEdges = totalNumberOfEdges + token.get();
            }
            context.write(key, new LongWritable(totalNumberOfEdges));
        }
    }
}

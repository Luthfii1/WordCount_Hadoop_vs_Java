<h1>
Hadoop vs Java Wordcount
</h1>

## kelompok 1

Roy Oswaldha 2106731592

Leonardo Jeremy pongpare 2106707914

Luthfi Misbachul 2106706981

Ivan Indrastata 2106706981

## **About**
Repositori ini berisi panduan instalasi Hadoop di sistem operasi Linux, terutama Ubuntu 22.04. Hadoop digunakan untuk menjalankan program Wordcount yang berfungsi untuk menghitung jumlah kata dalam sebuah file teks. Nantinya, program Wordcount Hadoop akan dibandingkan dengan program Wordcount menggunakan Java tanpa Hadoop.

## **Penginstalan Hadoop pada Ubuntu 22.04**
### 1.  Melakukan instalasi java 
```
sudo apt install openjdk-8-jdk
```
### 2. Pindah direktori ke java
```
cd /usr/lib/jvm
```
### 3. Konfigurasi open.bashrc 
```
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64 
export PATH=$PATH:/usr/lib/jvm/java-8-openjdk-amd64/bin 
export HADOOP_HOME=~/hadoop-3.2.3/ 
export PATH=$PATH:$HADOOP_HOME/bin 
export PATH=$PATH:$HADOOP_HOME/sbin 
export HADOOP_MAPRED_HOME=$HADOOP_HOME 
export YARN_HOME=$HADOOP_HOME 
export HADOOP_CONF_DIR=$HADOOP_HOME/etc/hadoop 
export HADOOP_COMMON_LIB_NATIVE_DIR=$HADOOP_HOME/lib/native 
export HADOOP_OPTS="-Djava.library.path=$HADOOP_HOME/lib/native" 
export HADOOP_STREAMING=$HADOOP_HOME/share/hadoop/tools/lib/hadoop-streaming-3.2.3.jar
export HADOOP_LOG_DIR=$HADOOP_HOME/logs 
export PDSH_RCMD_TYPE=ssh
```
### 4. Install SSH
```
sudo apt-get install ssh
```
### 5. Download Hadoop 3.2.2
```
https://hadoop.apache.org/release/3.2.2.html
```
### 6. Extract file
```
tar -zxvf ~/Downloads/hadoop-3.2.3.tar.gz
```
### 7. Pindah ke folder Hadoop
```
cd hadoop-3.2.3/etc/hadoop
```
### 8. Melakukan konfigurasi hadoop-env.h
```
now open hadoop-env.h
sudo nano hadoop-env.h
```
### 9. Cari JAVA_HOME dan lakukan perubahan
```
JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
```
### 10.	Melakukan konfigurasi pada file 
```
cd etc/hadoop
```
core-site.xml
```
<configuration> 
<property> 
<name>fs.defaultFS</name> 
<value>hdfs://localhost:9000</value> </property> 
<property> 
<name>hadoop.proxyuser.dataflair.groups</name> <value>*</value> 
</property> 
<property> 
<name>hadoop.proxyuser.dataflair.hosts</name> <value>*</value> 
</property> 
<property> 
<name>hadoop.proxyuser.server.hosts</name> <value>*</value> 
</property> 
<property> 
<name>hadoop.proxyuser.server.groups</name> <value>*</value> 
</property> 
</configuration>
```
hdfs-site.xml
```
<configuration> 
<property> 
<name>dfs.replication</name> 
<value>1</value> 
</property> 
</configuration>
```
mapred-site.xml
```
<configuration> 
<property> 
<name>mapreduce.framework.name</name> <value>yarn</value> 
</property> 
<property>
<name>mapreduce.application.classpath</name> 

<value>$HADOOP_MAPRED_HOME/share/hadoop/mapreduce/*:$HADOOP_MAPRED_HOME/share/hadoop/mapreduce/lib/*</value> 
</property> 
</configuration>
```
yarn-site.xml
```
<configuration> 
<property> 
<name>yarn.nodemanager.aux-services</name> 
<value>mapreduce_shuffle</value> 
</property> 
<property> 
<name>yarn.nodemanager.env-whitelist</name> 

<value>JAVA_HOME,HADOOP_COMMON_HOME,HADOOP_HDFS_HOME,HADOOP_CONF_DIR,CLASSPATH_PREP END_DISTCACHE,HADOOP_YARN_HOME,HADOOP_MAPRED_HOME</value> 
</property> 
</configuration>
```
### 11.	Jalankan ssh
```
ssh localhost 
ssh-keygen -t rsa -P '' -f ~/.ssh/id_rsa 
cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys 
chmod 0600 ~/.ssh/authorized_keys 
hadoop-3.2.3/bin/hdfs namenode -format
```
### 12.	Format file sistem
```
export PDSH_RCMD_TYPE=ssh
```
### 13.	Menjalankan Hadoop
```
start-all.sh
```

## **Menjalakan wordcount pada Hadoop**
### 1. Format
```
hadoop-3.2.3/bin/hdfs namenode -format
```
### 2. Jalankan hadoop
```
start-all.sh
```
### 3. Buat direktori input 
```
hadoop fs -mkdir /input
```
### 4. Siapkan file yang ingin dihitung, kemudian pindah file tersebut ke direktori input
```
hadoop fs -put text.txt /input
```
### 5. Buat program java untuk wordcount dan simpan dengan nama WordCount.java
```
sudo  nano WordCount.java
```
### 6. Contoh program java untuk wordcount
```
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
public class WordCount {
    // Map function
    public static class MyMapper extends Mapper<LongWritable, Text, Text, IntWritable>{
         private Text word = new Text();
         public void map(LongWritable key, Text value, Context context) 
                 throws IOException, InterruptedException {
             // Splitting the line on spaces
             String[] stringArr = value.toString().split("\\s+");
             for (String str : stringArr) {
                 word.set(str);
                 context.write(word, new IntWritable(1));
             }           
         }
    }

    // Reduce function
    public static class MyReducer extends Reducer<Text, IntWritable, Text, IntWritable>{        
        private IntWritable result = new IntWritable();
        public void reduce(Text key, Iterable<IntWritable> values, Context context) 
                throws IOException, InterruptedException {
          int sum = 0;
          for (IntWritable val : values) {
            sum += val.get();
          }
          result.set(sum);
          context.write(key, result);
        }
    }
    public static void main(String[] args)  throws Exception{
        Configuration conf = new Configuration();

        Job job = Job.getInstance(conf, "WC");
        job.setJarByClass(WordCount.class);
        job.setMapperClass(MyMapper.class);    
        job.setReducerClass(MyReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
```
### 7. Export classpath
```
export HADOOP_CLASSPATH=$($HADOOP_HOME/bin/hadoop classpath)
```
### 8. Buat folder untuk menyimpan hasil WordCount.java
```
sudo mkdir WordCountCompiled
```
### 9. Ubah permission pada folder tersebut
```
sudo chmod -R 777 WordCountCompiled
```
### 10.	Compile WordCount.java
```
javac -classpath $HADOOP_CLASSPATH -d WordCountCompiled/WordCount.java
```
### 11. Mengubah file executable .jar
```
jar -cvf WordCount.jar -C WordCountCompiled/.
```
### 12.	Menjalankan jar tersebut untuk menghitung jumlah kata pada file text.txt
```
hadoop jar WordCount.jar WordCount /input/text.txt /WordCount-Result
```
### 13.	Melihat hasil perhitungan wordcount
```
hadoop fs -cat /WordCount-Result/part-r-00000
```

## **Menjalankan program wordcount java tanpa hadoop**
### 1. Buka file WordCount.java di atas
![image](https://github.com/Luthfii1/WordCount_Hadoop_vs_Java/assets/72743765/35d6bb6d-0cbf-4ee2-8411-94ae7e83332c)
### 2. Ubah filePath sesuai letak file yang ingin dihitung
![image](https://github.com/Luthfii1/WordCount_Hadoop_vs_Java/assets/72743765/fb120cb5-cbc4-4564-9488-de5e6276be0d)
### 3. Klik tombol panah hijau pada public class Main
![image](https://github.com/Luthfii1/WordCount_Hadoop_vs_Java/assets/72743765/a2877fad-ae89-4eba-ab44-d8604908d7c7)
### 4. Ubah konfigurasi saat menjalankan program
![image](https://github.com/Luthfii1/WordCount_Hadoop_vs_Java/assets/72743765/21b2d210-3c87-4792-86e9-426d957a547f)
### 5. Tambahkan perintah -Xms16g
![image](https://github.com/Luthfii1/WordCount_Hadoop_vs_Java/assets/72743765/1b9848b4-90e1-4387-b487-b5e2d001e973)
>-Xms16g agar program java tersebut dapat menjalankan program yang dapat memakai hingga 16 GB RAM. Digunakan untuk menghindari error outofmemory.
### 6. Jalankan program

### **Text yang digunakan untuk percobaan**
Text berukuran 1 MB bernama onemb.txt yang sudah disertakan di repository ini

Text berukuran 10 MB bernama tenmb.txt yang sudah disertakan di repository ini 

Text berukuran 100 MB, 200 MB, 500 MB, dan 1 GB yang bersumber dari - https://www.i3s.unice.fr/~jplozi/hadooplab_lsds_2015/datasets/


### **Referensi**
- https://codewitharjun.medium.com/install-hadoop-on-ubuntu-operating-system-6e0ca4ef9689
- https://www.youtube.com/watch?v=Slbi-uzPtnw
- http://malifauzi.lecture.ub.ac.id/2019/04/tutorial-pembuatan-program-wordcount-pada-hadoop-menggunakan-java/
- https://www.i3s.unice.fr/~jplozi/hadooplab_lsds_2015/datasets/

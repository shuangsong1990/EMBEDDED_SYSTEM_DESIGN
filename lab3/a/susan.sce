<!DOCTYPE SCE>
<sce>
 <project>susan.sce</project>
 <compiler>
  <option name="libpath" ></option>
  <option name="libs" ></option>
  <option name="incpath" ></option>
  <option name="importpath" >.</option>
  <option name="defines" ></option>
  <option name="undefines" ></option>
  <option verbosity="3" warning="2" name="options" ></option>
 </compiler>
 <simulator>
  <option type="0" name="tracing" calls="False" debug="False" ></option>
  <option type="2" name="terminal" >xterm -title %e -e</option>
  <option name="logging" enabled="False" >.log</option>
  <option name="command" >/usr/bin/time ./%e &amp;&amp; diff -s out.pgm golden.pgm</option>
 </simulator>
 <models>
  <item type="" name="SusanSpec.sir" />
 </models>
 <imports>
  <item name="c_double_handshake" />
  <item name="c_double_handshake_signal" />
  <item name="c_int7220_queue" />
  <item name="c_queue" />
  <item name="c_uchar7220_queue" />
  <item name="design" />
  <item name="detect_edges" />
  <item name="edge_draw" />
  <item name="i_receive" />
  <item name="i_receiver" />
  <item name="i_send" />
  <item name="i_sender" />
  <item name="i_tranceiver" />
  <item name="monitor" />
  <item name="read_image" />
  <item name="setup_brightness_lut" />
  <item name="stimulus" />
  <item name="susan" />
  <item name="susan_edges" />
  <item name="susan_thin" />
  <item name="write_image" />
 </imports>
 <sources>
  <item name="./c_double_handshake_signal.sc" />
  <item name="./c_int7220_queue.sc" />
  <item name="./c_uchar7220_queue.sc" />
  <item name="./design.sc" />
  <item name="./detect_edges.sc" />
  <item name="./edge_draw.sc" />
  <item name="./monitor.sc" />
  <item name="./read_image.sc" />
  <item name="./setup_brightness_lut.sc" />
  <item name="./stimulus.sc" />
  <item name="./susan.sc" />
  <item name="./susan_edges.sc" />
  <item name="./susan_thin.sc" />
  <item name="./write_image.sc" />
  <item name="/usr/include/_G_config.h" />
  <item name="/usr/include/alloca.h" />
  <item name="/usr/include/bits/mathcalls.h" />
  <item name="/usr/include/bits/sigset.h" />
  <item name="/usr/include/bits/sys_errlist.h" />
  <item name="/usr/include/bits/time.h" />
  <item name="/usr/include/bits/types.h" />
  <item name="/usr/include/gconv.h" />
  <item name="/usr/include/libio.h" />
  <item name="/usr/include/math.h" />
  <item name="/usr/include/stdio.h" />
  <item name="/usr/include/stdlib.h" />
  <item name="/usr/include/string.h" />
  <item name="/usr/include/sys/select.h" />
  <item name="/usr/include/sys/types.h" />
  <item name="/usr/include/time.h" />
  <item name="/usr/include/wchar.h" />
  <item name="/usr/lib/gcc/i386-redhat-linux/3.4.6/include/stdarg.h" />
  <item name="/usr/lib/gcc/i386-redhat-linux/3.4.6/include/stddef.h" />
  <item name="/usr/local/packages/sce-20100908/inc/bits/huge_val.h" />
  <item name="/usr/local/packages/sce-20100908/inc/bits/pthreadtypes.h" />
  <item name="/usr/local/packages/sce-20100908/inc/sim.sh" />
  <item name="/usr/local/packages/sce-20100908/inc/sim/bit.sh" />
  <item name="/usr/local/packages/sce-20100908/inc/sim/longlong.sh" />
  <item name="/usr/local/packages/sce-20100908/inc/sim/time.sh" />
  <item name="c_double_handshake.sc" />
  <item name="c_queue.sc" />
  <item name="i_receive.sc" />
  <item name="i_receiver.sc" />
  <item name="i_send.sc" />
  <item name="i_sender.sc" />
  <item name="i_tranceiver.sc" />
  <item name="susan.sh" />
  <item name="susan_edge_detector.sc" />
 </sources>
 <metrics>
  <option name="types" >char[10000],pthread_barrier_t,unsigned long int[2],unsigned long long int,char[48],int,unsigned short int[3],float,char[40],int[2],char,pthread_mutex_t,struct __pthread_internal_slist,struct _IO_marker,__fsid_t,char[1],pthread_mutexattr_t,struct __gconv_step_data,pthread_cond_t,unsigned short int,long long int,event,char[100],__sigset_t,short int,struct _IO_FILE,long double,int[7220],fd_set,div_t,pthread_condattr_t,_LIB_VERSION_TYPE,struct random_data,bool,int[9],char[24],__u_quad_t,ldiv_t,unsigned short int[7],struct exception,void,unsigned long int[32],char[32],__mbstate_t,struct __gconv_info,unsigned char[516],void*,struct drand48_data,__huge_val_t,long int[2],char[200],unsigned char,_G_iconv_t,struct __gconv_step,unsigned char[7220],char[4],char[21],struct timespec,long int,pthread_rwlockattr_t,pthread_barrierattr_t,unsigned int,struct __gconv_trans_data,unsigned char[8],struct timeval,struct __gconv_step_data[1],unsigned long int,long int[32],double,enum __codecvt_result,_G_fpos_t,__quad_t,pthread_attr_t,pthread_rwlock_t,_G_fpos64_t,char[36],char[20],char[8]</option>
  <option name="operations" ></option>
 </metrics>
</sce>

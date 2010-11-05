package model;



public enum UUID { data ; public final int ___size___=14 ;}
enum Id{UUID}
enum Utf8$16{bytes; public final int ___size___=16; public String ___doc___="remove trailing 0's";}
enum Utf8$64{bytes; public final int ___size___=64 ;public String ___doc___="remove trailing 0's";}
enum Utf8$128{bytes; public final int ___size___=128; public String ___doc___="remove trailing 0's";}
enum Utf8$256{bytes; public final int ___size___=256 ;public String ___doc___="remove trailing 0's";}
enum Utf8$1024{bytes; public final int ___size___=1024; public String ___doc___="remove trailing 0's";}
enum Utf8$8192{bytes; public final int ___size___=8192;public String ___doc___="remove trailing 0's";}
enum HostName{Utf8$128}
enum Name{Utf8$128}
enum Email{Utf8$128}
enum Password{Utf8$16}
enum Account{Id,Name,Email,Password}
enum PascalString{len{public final int ___size___=1;},bytes{public final int ___size___=255;}}
enum Union{type{ public final int ___size___=1;},PascalString{public final int ___seek___=1;},Number{public final int ___seek___=1,___size___=10;}}


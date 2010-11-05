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
enum PascalString{len(1),bytes(255)}
enum Any{type(1),PascalString(0/** corrected by reflection assembly at generate time. */,1),Number(10,1)}


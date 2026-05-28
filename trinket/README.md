# Trinkets
its a simple abstraction layer allowing you to either access Curio or Accessories natively so modpack authors can freely 
decide what to use.
If a crash is desired when neither is loaded run 
``NucleusTrinket.ensureInitialized();``

Its based uppon a common Trinket class that you can register by calling
```java
Item item;
Trinket trinket behaviour;
NucleusTrinket.registerItem(item, behaviour);
```
Nucleus Trinkets also adds a new Component for items, the 
```AdditionalSlotComponent```.
Its simply a list of slots, and the list of slots are given to curio/accessories as additions to the items allowed slots.
This works with Curio too, allowing you to dynamically adjust the slots of an item.

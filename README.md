android-layout-engine
=====================

[Experimental project] An engine for achieving granular control over the visual appearance and data persistence over views/widgets displayed in any mobile app.

Usage:

If you need to build a simple layout,

                        JsonObject layout = new JsonObject(); // this layout is the layout sent from server
                        SimpleLayoutBuild builder = LayoutBuilderFactory.createSimpleLayoutBuilder(this);
                        View view = builder.build((ViewGroup)this.getWindow().getDecorView(),layout);
                        // now you have a dynamic view

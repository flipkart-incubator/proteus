android-layout-engine
=====================

[Experimental project] An engine for achieving granular control over the visual appearance and data persistence over views/widgets displayed in any mobile app.

Usage:

If you need to build a simple layout,

                        JsonObject layout = new JsonObject(); // this layout is the layout sent from server
                        SimpleLayoutBuild builder = LayoutBuilderFactory.createSimpleLayoutBuilder(this);
                        LayoutBuilderCallback callback = new LayoutBuilderCallback() {
                                    @Override
                                    public void onUnknownAttribute(ParserContext context, String attribute, final JsonElement element, final JsonObject object, View view) {
                        //                Log.d(TAG,"Unknown attribute "+attribute+" encountered for "+object);
                        //                if("onclick".equals(attribute))
                        //                {
                        //                    view.setOnClickListener(new View.OnClickListener() {
                        //                        @Override
                        //                        public void onClick(View v) {
                        //                           Toast.makeText(MainActivity.this,"View "+object+" clicked.",Toast.LENGTH_SHORT).show();
                        //                        }
                        //                    });
                        //                }
                                    }
                        
                                    @Override
                                    public View onUnknownViewType(ParserContext context, String viewType, JsonObject object, ViewGroup parent) {
                        
                                        //Log.e(TAG,"Unknown View "+viewType+" encountered. "+object);
                                        return null;
                                    }
                                };
                        builder.setListener(callback);
                        View view = builder.build((ViewGroup)this.getWindow().getDecorView(),layout);
                        // now you have a dynamic view

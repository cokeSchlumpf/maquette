import React from 'react';
import './styles.scss';

import {
    Breadcrumb,
    BreadcrumbItem,
    Button,
    Tabs,
    Tab,
} from 'carbon-components-react';

export default () => {
    const props = {
        tabs: {
            selected: 0,
            triggerHref: '#',
            role: 'navigation',
        },
        tab: {
            href: '#',
            role: 'presentation',
            tabIndex: 0,
        },
    };

    return (
        <div className="bx--grid bx--grid--full-width landing-page">
            <div className="bx--row landing-page__banner">
                <div className="bx--col-lg-16">
                    <Breadcrumb noTrailingSlash aria-label="Page navigation">
                        <BreadcrumbItem>
                            <a href="/">Dashboard</a>
                        </BreadcrumbItem>
                    </Breadcrumb>
                    <h1 className="landing-page__heading">
                        Welcome on your Dashboard, Hippo!
                    </h1>

                        <div className="bx--row">
                            <div className="bx--col-lg-10">
                                <p className="landing-page__p">
                                    Carbon is IBM’s open-source design system for digital
                                    products and experiences. With the IBM Design Language
                                    as its foundation, the system consists of working code,
                                    design tools and resources, human interface guidelines,
                                    and a vibrant community of contributors.
                                </p>
                            </div>
                            <div className="bx--col-lg-6" />
                        </div>
                </div>
            </div>
            <div className="bx--row landing-page__r2">
                <div className="bx--col bx--no-gutter">
                    <Tabs {...props.tabs} aria-label="Tab navigation">
                        <Tab {...props.tab} label="Summary">
                            <div className="bx--grid bx--grid--no-gutter bx--grid--full-width">
                                <div className="bx--row landing-page__tab-content">
                                    <div className="bx--col-md-4 bx--col-lg-7">
                                        <h2 className="landing-page__subheading">
                                            What is Carbon?
                                        </h2>
                                        <p className="landing-page__p">
                                            Carbon is IBM’s open-source design system for digital
                                            products and experiences. With the IBM Design Language
                                            as its foundation, the system consists of working code,
                                            design tools and resources, human interface guidelines,
                                            and a vibrant community of contributors.
                                        </p>
                                        <Button>Learn more</Button>
                                    </div>
                                    <div className="bx--col-md-4 bx--offset-lg-1 bx--col-lg-8">
                                    </div>
                                </div>
                            </div>
                        </Tab>
                        <Tab {...props.tab} label="Notifications">
                            <div className="bx--grid bx--grid--no-gutter bx--grid--full-width">
                                <div className="bx--row landing-page__tab-content">
                                    <div className="bx--col-lg-16">
                                        Rapidly build beautiful and accessible experiences. The Carbon kit
                                        contains all resources you need to get started.
                                    </div>
                                </div>
                            </div>
                        </Tab>
                    </Tabs>
                </div>
            </div>
        </div>
    )
};
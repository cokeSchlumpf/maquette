import _ from 'lodash';
import React from 'react';

import { Link } from 'react-router-dom';

import './styles.scss';
import {Breadcrumb, BreadcrumbItem} from "carbon-components-react";

import ContentContainer from '../ContentContainer'

const breadcrumbsDefault = [
    {
        name: "Home",
        to: "/"
    }
];

export default ({
                    breadcrumbsItems = breadcrumbsDefault,
                    title = "Title",
                    description,
                    showDescription = true,
                    centered = false }) => {
    const breadcrumbs = _.map(breadcrumbsItems, item => {
        return (
            <BreadcrumbItem key={ item.name }>
                <Link to={ item.to }>{ item.name }</Link>
            </BreadcrumbItem>);
    });

    const bannerContent = (
        <>
            <Breadcrumb noTrailingSlash aria-label="Page navigation">
                { breadcrumbs }
            </Breadcrumb>

            <h1 className="mq--page-banner--heading">
                { title }
            </h1>

            <div className="bx--row">
                <div className="bx--col-lg-10">
                    {
                        (description && <p className="mq--page-banner--p">{ description }</p>) ||
                        (showDescription && <p className="mq--page-banner--p-no-content">No description yet.</p>)
                    }
                </div>
                <div className="bx--col-lg-6"/>
            </div>
        </>
    );

    return (
        <div className="bx--grid bx--grid--full-width mq--page-banner">
            <div className="bx--row mq--page-banner--banner">
                <div className="bx--col-lg-16">
                    {
                        (centered && <ContentContainer>{ bannerContent } </ContentContainer>) ||
                        bannerContent
                    }
                </div>
            </div>
        </div>);
}
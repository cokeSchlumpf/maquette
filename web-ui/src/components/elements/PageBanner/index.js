import _ from 'lodash';
import cx from 'classnames'
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
                    tabSpace = false,
                    centered = false }) => {

    const bannerClassName = cx({
        'bx--row': true,
        'mq--page-banner--banner': true,
        'mq--page-banner--banner-tabs': tabSpace
    });

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
                <div className="bx--col-lg-14">
                    {
                        (description && <p className="mq--page-banner--p">{ description }</p>) ||
                        (showDescription && <p className="mq--page-banner--p-no-content">No description yet.</p>)
                    }
                </div>
                <div className="bx--col-lg-2"/>
            </div>
        </>
    );

    return (
        <div className="bx--grid bx--grid--full-width mq--page-banner">
            <div className={ bannerClassName }>
                <div className="bx--col-lg-16">
                    {
                        (centered && <ContentContainer>{ bannerContent } </ContentContainer>) ||
                        bannerContent
                    }
                </div>
            </div>
        </div>);
}